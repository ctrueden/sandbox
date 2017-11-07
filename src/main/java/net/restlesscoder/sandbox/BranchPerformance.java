
package net.restlesscoder.sandbox;

/**
 * A stupid micro-benchmark for branching performance.
 * <p>
 * It's not shocking that branching in a loop can have a performance impact.
 * </p>
 */
public class BranchPerformance {

	private static final long ITER = 10000000000L;

	public static boolean condition(long q) {
		return q > ITER / 2;
	}

	public static void main(final String... args) throws Exception {
		timeCondition();
		timeNoCondition();
	}

	public static void timeCondition() {
		final long start = System.currentTimeMillis();
		long q = 0;
		for (long i = 0; i < ITER; i++) {
			if (condition(i)) q++;
		}
		final long end = System.currentTimeMillis();
		System.out.println((end - start) + " (q=" + q + ")");
	}

	public static void timeNoCondition() {
		final long start = System.currentTimeMillis();
		long q = 0;
		for (long i = 0; i < ITER; i++) {
			q++;
		}
		final long end = System.currentTimeMillis();
		System.out.println((end - start) + " (q=" + q + ")");
	}
}
