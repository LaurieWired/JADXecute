package jadx.gui.plugins.jadxscripting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.api.JavaClass;
import jadx.api.JavaMethod;
import jadx.api.JavaNode;
import jadx.api.JavaVariable;
import jadx.api.data.ICodeRename;
import jadx.api.data.impl.JadxCodeData;
import jadx.api.data.impl.JadxCodeRef;
import jadx.api.data.impl.JadxCodeRename;
import jadx.api.data.impl.JadxNodeRef;
import jadx.core.deobf.NameMapper;
import jadx.core.utils.exceptions.JadxRuntimeException;
import jadx.gui.jobs.TaskStatus;
import jadx.gui.settings.JadxProject;
import jadx.gui.treemodel.JClass;
import jadx.gui.treemodel.JField;
import jadx.gui.treemodel.JMethod;
import jadx.gui.treemodel.JNode;
import jadx.gui.treemodel.JPackage;
import jadx.gui.treemodel.JVariable;
import jadx.gui.ui.MainWindow;
import jadx.gui.ui.TabbedPane;
import jadx.gui.ui.codearea.ClassCodeContentPanel;
import jadx.gui.ui.codearea.CodeArea;
import jadx.gui.ui.panel.ContentPanel;
import jadx.gui.utils.CacheObject;
import jadx.gui.utils.JNodeCache;

public class RenameObjectHelper {
	private static final Logger LOG = LoggerFactory.getLogger(RenameObjectHelper.class);

	private MainWindow mainWindow;
	private CacheObject cache;
	private JNode source;
	private JNode node;
	private String newObjectName;
	private String oldObjectName;

    // Rename class, method, or field
    public String renameObject(MainWindow mainWindow, JNode node, String newObjectName) {
		this.oldObjectName = node.getName();
		this.mainWindow = mainWindow;
		this.cache = mainWindow.getCacheObject();
		this.source = node;
		this.node = replaceNode(node);
		this.newObjectName = newObjectName;
		rename(newObjectName);

		return "Renamed " + oldObjectName + " to " + newObjectName;
	}

	// Need the methods from jadx.gui.ui.dialog.RenameDialog but they 
	//  are private and let's not change all the access modifiers
    private JNode replaceNode(JNode node) {
		if (node instanceof JMethod) {
			JavaMethod javaMethod = ((JMethod) node).getJavaMethod();
			if (javaMethod.isClassInit()) {
				throw new JadxRuntimeException("Can't rename class init method: " + node);
			}
			if (javaMethod.isConstructor()) {
				// rename class instead constructor
				return node.getJParent();
			}
		}
		return node;
	}

    private void rename(String newName) {
		if (!checkNewName()) {
			return;
		}
		try {
			updateCodeRenames(set -> processRename(node, newName, set));
			refreshState();
		} catch (Exception e) {
			LOG.error("Rename failed", e);
		}
	}
	
	private boolean checkNewName() {
		String newName = newObjectName;
		if (newName.isEmpty()) {
			// use empty name to reset rename (revert to original)
			return true;
		}

		boolean valid = NameMapper.isValidIdentifier(newName);
		return valid;
	}

	private void processRename(JNode node, String newName, Set<ICodeRename> renames) {
		JadxCodeRename rename = buildRename(node, newName, renames);
		renames.remove(rename);
		JavaNode javaNode = node.getJavaNode();
		if (javaNode != null) {
			javaNode.removeAlias();
		}
		if (!newName.isEmpty()) {
			renames.add(rename);
		}
	}

	@NotNull
	private JadxCodeRename buildRename(JNode node, String newName, Set<ICodeRename> renames) {
		if (node instanceof JMethod) {
			JavaMethod javaMethod = ((JMethod) node).getJavaMethod();
			List<JavaMethod> relatedMethods = javaMethod.getOverrideRelatedMethods();
			if (!relatedMethods.isEmpty()) {
				for (JavaMethod relatedMethod : relatedMethods) {
					renames.remove(new JadxCodeRename(JadxNodeRef.forMth(relatedMethod), ""));
				}
			}
			return new JadxCodeRename(JadxNodeRef.forMth(javaMethod), newName);
		}
		if (node instanceof JField) {
			return new JadxCodeRename(JadxNodeRef.forFld(((JField) node).getJavaField()), newName);
		}
		if (node instanceof JClass) {
			return new JadxCodeRename(JadxNodeRef.forCls(((JClass) node).getCls()), newName);
		}
		if (node instanceof JPackage) {
			return new JadxCodeRename(JadxNodeRef.forPkg(((JPackage) node).getFullName()), newName);
		}
		if (node instanceof JVariable) {
			JavaVariable javaVar = ((JVariable) node).getJavaVarNode();
			return new JadxCodeRename(JadxNodeRef.forMth(javaVar.getMth()), JadxCodeRef.forVar(javaVar), newName);
		}
		throw new JadxRuntimeException("Failed to build rename node for: " + node);
	}

	private void updateCodeRenames(Consumer<Set<ICodeRename>> updater) {
		JadxProject project = mainWindow.getProject();
		JadxCodeData codeData = project.getCodeData();
		if (codeData == null) {
			codeData = new JadxCodeData();
		}
		Set<ICodeRename> set = new HashSet<>(codeData.getRenames());
		updater.accept(set);
		List<ICodeRename> list = new ArrayList<>(set);
		Collections.sort(list);
		codeData.setRenames(list);
		project.setCodeData(codeData);
		mainWindow.getWrapper().reloadCodeData();
	}

	private void refreshState() {
		mainWindow.getWrapper().reInitRenameVisitor();

		JNodeCache nodeCache = cache.getNodeCache();
		JavaNode javaNode = node.getJavaNode();

		List<JavaNode> toUpdate = new ArrayList<>();
		if (source != null && source != node) {
			toUpdate.add(source.getJavaNode());
		}
		if (javaNode != null) {
			toUpdate.add(javaNode);
			toUpdate.addAll(javaNode.getUseIn());
			if (node instanceof JMethod) {
				toUpdate.addAll(((JMethod) node).getJavaMethod().getOverrideRelatedMethods());
			}
		} else if (node instanceof JPackage) {
			processPackage(toUpdate);
		} else {
			throw new JadxRuntimeException("Unexpected node type: " + node);
		}
		Set<JClass> updatedTopClasses = toUpdate
				.stream()
				.map(JavaNode::getTopParentClass)
				.map(nodeCache::makeFrom)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());

		LOG.debug("Classes to update: {}", updatedTopClasses);

		refreshTabs(mainWindow.getTabbedPane(), updatedTopClasses);

		if (!updatedTopClasses.isEmpty()) {
			mainWindow.getBackgroundExecutor().execute("Refreshing",
					() -> refreshClasses(updatedTopClasses),
					(status) -> {
						if (status == TaskStatus.CANCEL_BY_MEMORY) {
							mainWindow.showHeapUsageBar();
						}
						if (node instanceof JPackage) {
							mainWindow.getTreeRoot().update();
						}
						mainWindow.reloadTree();
					});
		}
	}

	private void processPackage(List<JavaNode> toUpdate) {
		String rawFullPkg = ((JPackage) node).getFullName();
		String rawFullPkgDot = rawFullPkg + ".";
		for (JavaClass cls : mainWindow.getWrapper().getClasses()) {
			String clsPkg = cls.getClassNode().getClassInfo().getPackage();
			// search all classes in package
			if (clsPkg.equals(rawFullPkg) || clsPkg.startsWith(rawFullPkgDot)) {
				toUpdate.add(cls);
				// also include all usages (for import fix)
				toUpdate.addAll(cls.getUseIn());
			}
		}
	}

	private void refreshClasses(Set<JClass> updatedTopClasses) {
		if (updatedTopClasses.size() < 10) {
			// small batch => reload
			LOG.debug("Classes to reload: {}", updatedTopClasses.size());
			for (JClass cls : updatedTopClasses) {
				try {
					cls.reload(cache);
				} catch (Exception e) {
					LOG.error("Failed to reload class: {}", cls.getFullName(), e);
				}
			}
		} else {
			// big batch => unload
			LOG.debug("Classes to unload: {}", updatedTopClasses.size());
			for (JClass cls : updatedTopClasses) {
				try {
					cls.unload(cache);
				} catch (Exception e) {
					LOG.error("Failed to unload class: {}", cls.getFullName(), e);
				}
			}
		}
	}

	private void refreshTabs(TabbedPane tabbedPane, Set<JClass> updatedClasses) {
		for (Map.Entry<JNode, ContentPanel> entry : tabbedPane.getOpenTabs().entrySet()) {
			JClass rootClass = entry.getKey().getRootClass();
			if (updatedClasses.remove(rootClass)) {
				ClassCodeContentPanel contentPanel = (ClassCodeContentPanel) entry.getValue();
				CodeArea codeArea = (CodeArea) contentPanel.getJavaCodePanel().getCodeArea();
				codeArea.refreshClass();
			}
		}
	}
}

