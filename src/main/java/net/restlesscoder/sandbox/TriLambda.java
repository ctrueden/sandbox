
package net.restlesscoder.sandbox;

public class TriLambda {

	@FunctionalInterface
	public interface TernaryFunction {
		String apply(String a, String b, String c);
	}

	public static void main(final String... args) throws Exception {
		process((a, b, c) -> a + " " + b + " " + c);
		process((a, b, c) -> a + "|" + b + "|" + c);
	}

	private static void process(TernaryFunction f) {
		final String[][] args = {
			{"happy", "birthday", "!"},
			{"good", "night", "moon"}
		};
		for (final String[] arg : args) {
			System.out.println(f.apply(arg[0], arg[1], arg[2]));
		}
	}
}
