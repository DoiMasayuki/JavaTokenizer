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
	
	private boolean isDebugMode = false;
	
	public Tokenizer(Options options) {
		this.inputFiles = options.getInputFiles();
		this.isOutputStdin = options.isOutputStdin;
		this.output = options.output;
		this.isDebugMode = options.isDebugMode;
	}
	
	public void tokenize() throws IOException, InvalidInputException {
		System.err.println("### " + inputFiles.size() + " files tokinizing ...");
		StringBuilder sb = new StringBuilder();
		for (Path path : inputFiles) {
			String source = new String(Files.readAllBytes(path));
			IScanner scanner = ToolFactory.createScanner(false, false, true, "1.9");
			scanner.setSource(source.toCharArray());
			int tokens;
			try {
			while ((tokens = scanner.getNextToken()) != ITerminalSymbols.TokenNameEOF) {
				if (isDebugMode) {
					System.out.println(tokens + " | " + new String(scanner.getCurrentTokenSource()));
				}
				String token = this.replaceEscapeChar(new String(scanner.getCurrentTokenSource()));
				
				sb.append(token);
				sb.append(" ");
			}
			sb.append('\n');
			}catch( InvalidInputException e) {
				System.err.println("[ERROR] : can\'t parse this file "+path);
				e.printStackTrace();
			}
		}
		this.output(sb.toString());
		System.err.println("### finished tokenize");
	}
	
	private String replaceEscapeChar(String str) {
		
		String token = str.replaceAll(" ", "_").replaceAll("\t", "_").replaceAll("\r", "").replaceAll("\n", "");
		
		if (token.startsWith("'") && token.endsWith("'") && token.length()==3) {
			token = token.substring(1, token.length() - 1);
			StringBuilder sb = new StringBuilder("'");
			for (int i = 0; i < token.length(); i++) {
				sb.append(String.format("\\u%04X", Character.codePointAt(token, i)));
			}
			sb.append("'");
			token = sb.toString();
		}
		
		return token;
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
