import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import application.Main;
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

		Main.run();
	}
	@AfterClass
	public static void tearDown() {

		Main.exit();
	}
}
