import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import application.MainSpr;
import applicationTest.CanvasPaneTest;
import applicationTest.TextEditingTest;
import modelTest.SprinklerDAOTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({ 
   CanvasPaneTest.class ,TextEditingTest.class, SprinklerDAOTest.class
})

public class JunitTestSuite {
	@BeforeClass
	public static void setUp() {

		MainSpr.run();
	}
	@AfterClass
	public static void tearDown() {

		MainSpr.exit();
	}
}
