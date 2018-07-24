package masa.tokenizer;

import java.io.IOException;
import java.nio.file.Paths;

import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.junit.Test;

public class TokenizerTest {
	
	@Test
	public void Test() {
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.setInput(Paths.get(".\\sample\\example1.java"));
		tokenizer.setNormalize(true);
		try {
			tokenizer.tokenizeSubDirFile();
		} catch (IOException | InvalidInputException e) {
			e.printStackTrace();
		}
	}
	
}
