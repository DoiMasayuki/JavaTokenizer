package masa.tokenizer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;

public class Tokenizer {
	
	private ArrayList<Path> inputFiles = new ArrayList<Path>();
	private boolean isOutputStdin = true;
	private Path output;
	
	public Tokenizer(Options options) {
		this.inputFiles = options.getInputFiles();
		this.isOutputStdin = options.isOutputStdin;
		this.output = options.output;
	}
	
	public void tokenize() throws IOException, InvalidInputException {
		StringBuilder sb = new StringBuilder();
		for (Path path : inputFiles) {
			String source = new String(Files.readAllBytes(path));
			IScanner scanner = ToolFactory.createScanner(true, false, true, "1.9");
			scanner.setSource(source.toCharArray());
			while (scanner.getNextToken() != ITerminalSymbols.TokenNameEOF) {
				sb.append(new String(scanner.getCurrentTokenSource()).replaceAll(" ", "_"));
				sb.append(" ");
			}
			sb.append('\n');
		}
		this.output(sb.toString());
	}
	
	private void output(String tokenized) {
		if (this.isOutputStdin) {
			this.outputStdin(tokenized);
		} else {
			this.outputFile(tokenized);
		}
	}
	
	private void outputFile(String tokenized) {
		try {
			FileWriter fw = new FileWriter(output.toFile());
			fw.write(tokenized);
			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void outputStdin(String tokenized) {
		System.out.println(tokenized);
	}
	
	
	public static void main(String[] args) throws Exception {
		Options options = new Options(args);
		Tokenizer tokenizer = new Tokenizer(options);
		tokenizer.tokenize();
	}
}
