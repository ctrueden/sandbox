
package net.restlesscoder.sandbox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class RawGenericsWeirdness {

	public static <T> List<Set<T>> makeSets( //
		final Function<Integer, Set<T>> setFactory, int count)
	{
		final List<Set<T>> sets = new ArrayList<>();
		for (int i=0; i<count; i++) {
			sets.add(setFactory.apply(10));
		}
		return sets;
	}

	//	NB: Same weirdness occurs with Img instead of Set.
	public static void createSomeSets() {
		Function<Integer, Set<Float>> mySetFactory = i -> { Set<Float> set = new HashSet<>(); set.add(i.floatValue()); return set; };
//		List<Set<?>> sets = makeSets(mySetFactory, 3); // Type mismatch: cannot convert from List<Set<Float>> to List<Set<?>>
		Set<?> firstSet = makeSets(mySetFactory, 5).get(0);

		Function myRawSetFactory = mySetFactory;
		List<Set<?>> sets = makeSets(myRawSetFactory, 3);
//		Set<?> firstSet = makeSets(myRawSetFactory, 5).get(0); // Type mismatch: cannot convert from Object to Set<?>

		System.out.println(sets);
	}
}
