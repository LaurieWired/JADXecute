package jadx.gui.plugins.jadxscripting;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.event.PopupMenuEvent;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jadx.api.ICodeInfo;
import jadx.api.JavaMethod;
import jadx.api.JavaNode;
import jadx.api.data.ICodeComment;
import jadx.api.data.impl.JadxCodeComment;
import jadx.api.data.impl.JadxCodeData;
import jadx.api.data.impl.JadxCodeRef;
import jadx.api.data.impl.JadxNodeRef;
import jadx.api.metadata.ICodeAnnotation;
import jadx.api.metadata.ICodeAnnotation.AnnType;
import jadx.api.metadata.ICodeMetadata;
import jadx.api.metadata.ICodeNodeRef;
import jadx.api.metadata.annotations.InsnCodeOffset;
import jadx.api.metadata.annotations.NodeDeclareRef;
import jadx.gui.JadxWrapper;
import jadx.gui.treemodel.JClass;
import jadx.gui.ui.MainWindow;
import jadx.gui.ui.codearea.CodeArea;
import jadx.gui.ui.dialog.CommentDialog;
import jadx.gui.utils.DefaultPopupMenuListener;
import jadx.gui.utils.NLS;
import jadx.gui.utils.UiUtils;
import jadx.gui.settings.JadxProject;

import jadx.api.JavaClass;
import jadx.api.JavaVariable;
import jadx.api.data.ICodeRename;
import jadx.api.data.impl.JadxCodeRename;
import jadx.core.deobf.NameMapper;
import jadx.core.utils.exceptions.JadxRuntimeException;
import jadx.gui.jobs.TaskStatus;
import jadx.gui.treemodel.JField;
import jadx.gui.treemodel.JMethod;
import jadx.gui.treemodel.JNode;
import jadx.gui.treemodel.JPackage;
import jadx.gui.treemodel.JVariable;
import jadx.gui.ui.TabbedPane;
import jadx.gui.ui.codearea.ClassCodeContentPanel;
import jadx.gui.ui.panel.ContentPanel;
import jadx.gui.utils.CacheObject;
import jadx.gui.utils.JNodeCache;

public class AddCommentHelper {
    private static final Logger LOG = LoggerFactory.getLogger(AddCommentHelper.class);

    private MainWindow mainWindow;
    private CacheObject cache;
	private String newCommentString;
    private JavaNode node;
    private boolean updateComment;

    public AddCommentHelper(MainWindow mainWindow, JavaNode node) {
        this.mainWindow = mainWindow;
        this.cache = mainWindow.getCacheObject();
        this.node = node;
    }

    // To be implemented
    /*
    public String addVariableComment(String newCommentString, JavaVariable javaVariable) {
        this.newCommentString = newCommentString;

        ICodeComment blankComment = new JadxCodeComment(JadxNodeRef.forJavaNode(node), JadxCodeRef.forVar(javaVariable), "");
        if (blankComment == null) {
            return "Failed to add comment";
        }

        ICodeComment existComment = searchForExistComment(blankComment);
		if (existComment != null) {
            this.updateComment = true;
            apply(existComment, javaVariable);
		} else {
            this.updateComment = false;
            apply(blankComment, javaVariable);
		}

        return "Added comment in " + node.getFullName() + " for variable " + javaVariable.getName();
    }
    */

    public String addInstructionComment(String newCommentString, int commentSmaliOffset) {
        this.newCommentString = newCommentString;

        ICodeComment blankComment = new JadxCodeComment(JadxNodeRef.forJavaNode(node), JadxCodeRef.forInsn(commentSmaliOffset), "");
        if (blankComment == null) {
            return "Failed to add comment";
        }

        ICodeComment existComment = searchForExistComment(blankComment);
		if (existComment != null) {
            this.updateComment = true;
            apply(existComment, commentSmaliOffset);
		} else {
            this.updateComment = false;
            apply(blankComment, commentSmaliOffset);
		}

        return "Added comment in " + node.getFullName() + " at offset " + commentSmaliOffset;
    }

    private void apply(ICodeComment comment, int commentOffset) {
		if (newCommentString.isEmpty()) {
			if (updateComment) {
				updateCommentsData(list -> list.removeIf(c -> c == comment));
			}
			return;
		}
        
		ICodeComment newComment = new JadxCodeComment(JadxNodeRef.forJavaNode(node), JadxCodeRef.forInsn(commentOffset), newCommentString);
		if (updateComment) {
			updateCommentsData(list -> {
				list.remove(comment);
				list.add(newComment);
			});
		} else {
			updateCommentsData(list -> list.add(newComment));
		}
	}

    private void apply(ICodeComment comment, JavaVariable javaVariable) {
		if (newCommentString.isEmpty()) {
			if (updateComment) {
				updateCommentsData(list -> list.removeIf(c -> c == comment));
			}
			return;
		}
        
		ICodeComment newComment = new JadxCodeComment(JadxNodeRef.forJavaNode(node), JadxCodeRef.forVar(javaVariable), newCommentString);
		if (updateComment) {
			updateCommentsData(list -> {
				list.remove(comment);
				list.add(newComment);
			});
		} else {
			updateCommentsData(list -> list.add(newComment));
		}
	}

    private ICodeComment searchForExistComment(ICodeComment blankComment) {
		try {
			JadxProject project = mainWindow.getProject();
			JadxCodeData codeData = project.getCodeData();
			if (codeData == null || codeData.getComments().isEmpty()) {
				return null;
			}
			for (ICodeComment comment : codeData.getComments()) {
				if (Objects.equals(comment.getNodeRef(), blankComment.getNodeRef())
						&& Objects.equals(comment.getCodeRef(), blankComment.getCodeRef())) {
					return comment;
				}
			}
		} catch (Exception e) {
			LOG.error("Error searching for exists comment", e);
		}
		return null;
	}

	private String updateCommentsData(Consumer<List<ICodeComment>> updater) {
        JadxProject project = mainWindow.getProject();
        JadxCodeData codeData = project.getCodeData();

		try {
			if (codeData == null) {
				codeData = new JadxCodeData();
			}
			List<ICodeComment> list = new ArrayList<>(codeData.getComments());

			updater.accept(list);
			Collections.sort(list);
			codeData.setComments(list);
			project.setCodeData(codeData);
			mainWindow.getWrapper().reloadCodeData();
		} catch (Exception e) {
			LOG.error("Comment action failed", e);
		}
		try {
			refreshState();
		} catch (Exception e) {
			LOG.error("Failed to reload code", e);
		}

        String retval = "";

        for (ICodeComment comment : codeData.getComments()) {
            retval += comment.getComment() + "\n";
        }
        return retval;
	}

    private void refreshState() {
		mainWindow.getWrapper().reInitRenameVisitor();

		JNodeCache nodeCache = cache.getNodeCache();
		JavaNode javaNode = node;

		List<JavaNode> toUpdate = new ArrayList<>();
		if (javaNode != null) {
			toUpdate.add(javaNode);
			if (node instanceof JMethod) {
				toUpdate.addAll(((JMethod) node).getJavaMethod().getOverrideRelatedMethods());
			}
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
