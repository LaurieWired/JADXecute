package jadx.core.dex.attributes.nodes;

import java.util.ArrayList;
import java.util.List;

import jadx.api.ICodeWriter;
import jadx.api.plugins.input.data.attributes.IJadxAttribute;
import jadx.core.dex.attributes.AType;
import jadx.core.dex.instructions.PhiInsn;

public class PhiListAttr implements IJadxAttribute {

	private final List<PhiInsn> list = new ArrayList<>();

	@Override
	public AType<PhiListAttr> getAttrType() {
		return AType.PHI_LIST;
	}

	public List<PhiInsn> getList() {
		return list;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PHI: ");
		for (PhiInsn phiInsn : list) {
			sb.append('r').append(phiInsn.getResult().getRegNum()).append(' ');
		}
		for (PhiInsn phiInsn : list) {
			sb.append(ICodeWriter.NL).append("  ").append(phiInsn).append(' ').append(phiInsn.getAttributesString());
		}
		return sb.toString();
	}
}
