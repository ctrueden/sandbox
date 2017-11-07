
package net.restlesscoder.sandbox;

import java.util.ArrayList;

public class Ascii extends ArrayList<String> {

	public static void main(final String... args) {
		for (int i = 32; i < 127; i++) {
			if (i % 5 == 2) System.out.println();
			System.out.printf("\t%3d %s", i, (char) i);
		}
	}
}
