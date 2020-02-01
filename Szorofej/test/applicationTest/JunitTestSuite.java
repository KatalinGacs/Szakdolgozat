package applicationTest;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import application.MainSpr;

@RunWith(Suite.class)

@Suite.SuiteClasses({ 
   CanvasPaneTest.class ,TextEditingTest.class
})

public class JunitTestSuite {
	@BeforeClass
	public static void setUp() {

		MainSpr.run();

	}
}
