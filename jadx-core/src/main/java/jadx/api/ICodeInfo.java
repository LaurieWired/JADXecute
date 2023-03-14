package jadx.api;

import jadx.api.impl.SimpleCodeInfo;
import jadx.api.metadata.ICodeMetadata;

public interface ICodeInfo {

	ICodeInfo EMPTY = new SimpleCodeInfo("");

	String getCodeStr();

	// Temporary until secure method for updating code is added
	// TODO: swap for limited / safe code updates
	void setCodeStr(String codeStr);

	ICodeMetadata getCodeMetadata();

	boolean hasMetadata();
}
