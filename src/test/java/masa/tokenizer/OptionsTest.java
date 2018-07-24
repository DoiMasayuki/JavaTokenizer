package masa.tokenizer;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

public class OptionsTest {
	
	
	
	@Test
	public void testOptionsNomalize() {
		String[] args = { "--normalize" };
		Options options;
		try {
			options = new Options(args);
			assertTrue(options.isNormalize);
		} catch (CmdLineException e) {
			e.printStackTrace();
		}
	}
	
}
