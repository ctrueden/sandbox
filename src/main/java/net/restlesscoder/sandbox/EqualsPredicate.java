
package net.restlesscoder.sandbox;

import java.util.function.Predicate;

public class EqualsPredicate {

	public static void main(final String... args) {
		// Why do we need Predicate.isEqual anyway?
		// Why not just use obj::equals?
		// Here's why!

		final Object obj = null;

		Predicate<Object> objEqualsSafe = Predicate.isEqual(obj);
		System.out.println(objEqualsSafe.test(null));

		Predicate<Object> objEquals = obj::equals;
		System.out.println(objEquals.test(null));
	}

}
