
package net.restlesscoder.sandbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionReferenceRecursion {

	public static void main(final String... args) {
		final List<Object> l1 = new ArrayList<>(Arrays.asList(1, 2, 3));
		final List<Object> l2 = new ArrayList<>(Arrays.asList(4, 5, 6));

		System.out.println("-- BEFORE --");
		System.out.println("l1 = " + l1);
		System.out.println("l2 = " + l2);

		// Java is OK when a collection contains itself directly.
		l1.add(l1);
		l2.add(l2);
		System.out.println("-- DIRECT-REFERENCED --");
		System.out.println("l1 = " + l1);
		System.out.println("l2 = " + l2);

		// But Java fails when a collection contains itself indirectly.
		l1.add(l2);
		l2.add(l1);
		System.out.println("-- CROSS-REFERENCED --");
		System.out.println("l1 = " + l1);
		System.out.println("l2 = " + l2);
	}

}
