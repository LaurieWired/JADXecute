package jadx.gui.settings.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import jadx.api.data.impl.JadxCodeData;

public class ProjectData {

	private int projectVersion = 1;
	private List<Path> files = new ArrayList<>();
	private List<String[]> treeExpansions = new ArrayList<>();
	private JadxCodeData codeData = new JadxCodeData();
	private List<TabViewState> openTabs = Collections.emptyList();
	private int activeTab = -1;
	private @Nullable Path cacheDir;
	private boolean enableLiveReload = false;
	private List<String> searchHistory = new ArrayList<>();

	public List<Path> getFiles() {
		return files;
	}

	public void setFiles(List<Path> files) {
		this.files = Objects.requireNonNull(files);
	}

	public List<String[]> getTreeExpansions() {
		return treeExpansions;
	}

	public void setTreeExpansions(List<String[]> treeExpansions) {
		this.treeExpansions = treeExpansions;
	}

	public JadxCodeData getCodeData() {
		return codeData;
	}

	public void setCodeData(JadxCodeData codeData) {
		this.codeData = codeData;
	}

	public int getProjectVersion() {
		return projectVersion;
	}

	public void setProjectVersion(int projectVersion) {
		this.projectVersion = projectVersion;
	}

	public List<TabViewState> getOpenTabs() {
		return openTabs;
	}

	/**
	 *
	 * @param openTabs
	 * @return <code>true></code> if a change was saved
	 */
	public boolean setOpenTabs(List<TabViewState> openTabs) {
		if (this.openTabs.equals(openTabs)) {
			return false;
		}
		this.openTabs = openTabs;
		return true;
	}

	public int getActiveTab() {
		return activeTab;
	}

	/**
	 *
	 * @param activeTab
	 * @return <code>true></code> if a change was saved
	 */
	public boolean setActiveTab(int activeTab) {
		if (this.activeTab == activeTab) {
			return false;
		}
		this.activeTab = activeTab;
		return true;
	}

	@Nullable
	public Path getCacheDir() {
		return cacheDir;
	}

	public void setCacheDir(Path cacheDir) {
		this.cacheDir = cacheDir;
	}

	public boolean isEnableLiveReload() {
		return enableLiveReload;
	}

	public void setEnableLiveReload(boolean enableLiveReload) {
		this.enableLiveReload = enableLiveReload;
	}

	public List<String> getSearchHistory() {
		return searchHistory;
	}

	public void setSearchHistory(List<String> searchHistory) {
		this.searchHistory = searchHistory;
	}
}
