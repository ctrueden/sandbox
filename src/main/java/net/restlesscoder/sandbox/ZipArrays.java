
package net.restlesscoder.sandbox;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ZipArrays {

	public static void main(final String... args) throws Exception {
		final String[] a = { "hello", "cruel", "world" };
		final String[] b = { "how", "are", "you" };
		final String[] c = { "until", "next", "time" };
		System.out.println(Arrays.deepToString(zip(a, b, c)));
	}

	@SafeVarargs
	public static <T> T[][] zip(final T[]... arrays) {
		@SuppressWarnings("unchecked")
		final T[][] zipped = (T[][]) //
		Array.newInstance(arrays[0].getClass().getComponentType(), arrays[0].length, arrays.length);
		return pivot(arrays, zipped);
	}

	private static <T> T[][] pivot(final T[][] src, final T[][] dest) {
		for (int a = 0; a < src.length; a++) {
			for (int i = 0; i < src[a].length; i++) {
				dest[i][a] = src[a][i];
			}
		}
		return dest;
	}
}
