import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import application.Main;
import modelTest.SprinklerDAOTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({ 
   SprinklerDAOTest.class
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
