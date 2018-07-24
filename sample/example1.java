package masa.tokenizer;

public class example1 {
	public void main(String args[]) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
			sb.append(args[i]);
		}
		System.out.println("args"+" = "+sb.toString());
	}
}
