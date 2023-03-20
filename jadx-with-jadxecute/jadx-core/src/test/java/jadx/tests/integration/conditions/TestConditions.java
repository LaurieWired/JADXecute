package jadx.tests.integration.conditions;

import org.junit.jupiter.api.Test;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestConditions extends IntegrationTest {

	public static class TestCls {
		public boolean test(boolean a, boolean b, boolean c) {
			return (a && b) || c;
		}
	}

	@Test
	public void test() {
		ClassNode cls = getClassNode(TestCls.class);
		String code = cls.getCode().toString();

		assertThat(code, not(containsString("(!a || !b) && !c")));
		assertThat(code, containsString("return (a && b) || c;"));
	}
}
