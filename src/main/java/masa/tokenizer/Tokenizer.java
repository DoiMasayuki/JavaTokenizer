package masa.tokenizer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;

public class Tokenizer {
	
	private Options options = new Options();
	
	public Tokenizer(Options options) {
		this.options = options;
	}
	
	public Tokenizer() {
	}
	
	public String tokenizeSource(String source) throws InvalidInputException {
		IScanner scanner = ToolFactory.createScanner(false, false, true, "1.9");
		StringBuilder sb = new StringBuilder();
		scanner.setSource(source.toCharArray());
		int tokens;
		while ((tokens = scanner.getNextToken()) != ITerminalSymbols.TokenNameEOF) {
			if (options.isDebugMode) {
				System.out.println(tokens + " | " + new String(scanner.getCurrentTokenSource()));
			}
			String token = this.replaceEscapeChar(new String(scanner.getCurrentTokenSource()));
			
			if (options.isNormalize) {
				token = this.NomalizeToken(token, tokens);
			}
			sb.append(token);
			sb.append(" ");
		}
		sb.append('\n');
		return sb.toString();
	}
	
	public String tokenizeFile(Path path) throws IOException {
		String source = new String(Files.readAllBytes(path));
		try {
			return this.tokenizeSource(source);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String tokenizeSubDirFile() throws IOException, InvalidInputException {
		options.setInputFiles();
		System.err.println("### " + options.inputFiles.size() + " files tokinizing ...");
		StringBuilder sb = new StringBuilder();
		for (Path path : options.inputFiles) {
			sb.append(this.tokenizeFile(path));
		}
		this.output(sb.toString());
		System.err.println("### finished tokenize");
		return sb.toString();
	}
	
	private String NomalizeToken(String token, int tokens) {
		if (tokens == ITerminalSymbols.TokenNameStringLiteral) {
			token = "__StringLiteral";
		}
		if (tokens == ITerminalSymbols.TokenNameIdentifier) {
			token = "__Identifier";
		}
		return token;
	}
	
	private String replaceEscapeChar(String str) {
		
		String token = str.replaceAll(" ", "_").replaceAll("\t", "_").replaceAll("\r", "").replaceAll("\n", "");
		
		if (token.startsWith("'") && token.endsWith("'") && token.length() == 3) {
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
		if (this.options.isOutputStdin) {
			this.outputStdin(tokenized);
		} else {
			this.outputFile(tokenized);
		}
	}
	
	private void outputFile(String tokenized) {
		try {
			FileWriter fw = new FileWriter(options.output.toFile());
			fw.write(tokenized);
			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void outputStdin(String tokenized) {
		System.out.println(tokenized);
	}
	
	public void setShowUsage(boolean isShowUsage) {
		this.options.isShowUsage = isShowUsage;
	}
	
	public void setInput(Path input) {
		this.options.input = input;
	}
	
	public void setOutput(Path output) {
		this.options.output = output;
	}
	
	public void setNormalize(boolean isNormalize) {
		this.options.isNormalize = isNormalize;
	}
	
	public static void main(String[] args) throws Exception {
		Options options = new Options(args);
		Tokenizer tokenizer = new Tokenizer(options);
		tokenizer.tokenizeSubDirFile();
	}
}
