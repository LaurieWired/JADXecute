package jadx.tests.integration.others;

import org.junit.jupiter.api.Test;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestStaticMethod extends IntegrationTest {

	public static class TestCls {
		static {
			f();
		}

		private static void f() {
		}
	}

	@Test
	public void test() {
		ClassNode cls = getClassNode(TestCls.class);
		String code = cls.getCode().toString();

		assertThat(code, containsString("static {"));
		assertThat(code, containsString("private static void f() {"));
	}
}
