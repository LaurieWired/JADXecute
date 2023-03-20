package jadx.tests.integration.names;

import java.util.List;

import org.junit.jupiter.api.Test;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public class TestReservedPackageNames extends SmaliTest {

	// @formatter:off
	/*
		package do.if;

		public class A {}
	*/
	// @formatter:on

	@Test
	public void test() {
		List<ClassNode> clsList = loadFromSmaliFiles();
		for (ClassNode cls : clsList) {
			String code = cls.getCode().toString();
			assertThat(code, not(containsString("package do.if;")));
		}
	}

	@Test
	public void testDeobf() {
		enableDeobfuscation();
		List<ClassNode> clsList = loadFromSmaliFiles();
		for (ClassNode cls : clsList) {
			String code = cls.getCode().toString();
			assertThat(code, not(containsString("package do.if;")));
		}
	}

	@Test
	public void testRenameDisabled() {
		args.setRenameCaseSensitive(false);
		args.setRenameValid(false);
		args.setRenamePrintable(false);

		disableCompilation();

		List<ClassNode> clsList = loadFromSmaliFiles();
		for (ClassNode cls : clsList) {
			String code = cls.getCode().toString();
			if (cls.getShortName().equals("A")) {
				assertThat(code, containsString("package do.if;"));
			}
		}
	}
}
