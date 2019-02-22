
package net.restlesscoder.sandbox;

public class PlusEqualsCasts {

	public static void main(final String... args) {
		int x = 5;
		System.out.println("x -> " + x);

		// The following does not compile.
		//x = x + 3 * 1.9;
		//System.out.println("x -> " + x);

		// But the following does compile!!
		x += 3 * 1.9;
		System.out.println("x -> " + x);
		x *= 2.65;
		System.out.println("x -> " + x);
	}

}
