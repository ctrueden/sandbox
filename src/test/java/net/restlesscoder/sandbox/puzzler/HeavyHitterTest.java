package net.restlesscoder.sandbox.puzzler;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * The Heavy Hitter problem: exercise 22.45 (page 499) of "Elements of
 * Programming Interviews in Java: The Insiders' Guide" by Adnan Aziz.
 */
public class HeavyHitterTest {

	private final Random rng = new Random(0x12345678);

	@Test
	public void testSolution() {
		final int n = 10, k = 2, iter = 10_000_000;
		System.out.println("n = " + n + ", k = " + k);
		final int[] array = new int[n];
		for (int q=0; q<iter; q++) {
			System.out.println("========== ITERATION " + q + "==========");
			for (int i=0; i<n; i++) array[i] = rng.nextInt(10) + 1; // [1, 10]
			System.out.println("array = " + Arrays.toString(array));
			final Set<Integer> expected = easySolution(array, k);
			final Set<Integer> actual = norSolution(array, k);
			assertEquals(expected, actual);
		}
	}

	/** An easy-to-implement solution requiring O(n) additional space. */
	private Set<Integer> easySolution(final int[] array, final int k) {
		final int n = array.length;
		final int threshold = (int) Math.ceil(n / k);

		final Map<Integer, Integer> counts = new HashMap<>();
		for (int i=0; i<n; i++) {
			counts.merge(array[i], 1, (key, val) -> val + 1);
		}
		return counts.keySet().stream().filter(key -> counts.get(key) > threshold).collect(Collectors.toSet());
	}

	/** NOR's wacky solution. */
	private Set<Integer> norSolution(final int[] array, final int k) {
		final int n = array.length;
		final int threshold = (int) Math.ceil(n / k);

		final Set<Integer> set = new HashSet<>();

		final int[] candidates = new int[k];
		final int[] counts = new int[k];

		// First pass: identify candidates for sufficiency.
		for (int i=0; i<n; i++) {
			System.out.println("array[" + i + "] = " + array[i]);
			boolean found = false;
			for (int j=0; j<k; j++) {
				if (candidates[j] == array[i]) {
					// This guy is already a candidate; increment the count.
					counts[j] += k;
					found = true;
				}
				else {
					// Penalize non-matching candidate.
					counts[j]--;
					if (counts[j] <= 0) {
						// Delete sufficiently penalized candidate.
						candidates[j] = counts[j] = 0;
					}
				}
			}
			if (!found) {
				// Add this guy as a candidate.
				boolean success = false;
				for (int j=0; j<k; j++) {
					if (candidates[j] == 0) {
						candidates[j] = array[i];
						counts[j] = k;
						success = true;
						break;
					}
				}
				if (!success) throw new IllegalStateException("Candidates list unexpectedly full!");
			}
			// Debug.
			for (int j=0; j<k; j++) {
				final String message = candidates[j] == 0 ? "EMPTY" : (candidates[j] + " -> " + counts[j]);
				System.out.println("\t#" + j + ": " + message);
			}
		}

		// Second pass: check each remaining candidate for sufficiency.
		Arrays.fill(counts, 0);
		for (int i=0; i<n; i++) {
			for (int j=0; j<k; j++) {
				if (candidates[j] == array[i]) {
					counts[j]++;
					if (counts[j] > threshold) set.add(candidates[j]);
				}
			}
		}

		return set;
	}
}
