
package net.restlesscoder.sandbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DoubleBraces extends ArrayList<String> {

	{
		add("{}");
	}
	{
		add("{} again");
	}

	DoubleBraces() {
		add("ctor()");
	}

	DoubleBraces(final boolean flag) {
		add("ctor(" + flag + ")");
	}

	public static void main(final String... args) {
		final DoubleBraces db = new DoubleBraces(false);
		System.out.println(db);
		final List<String> l = Arrays.asList("{}", "{} again", "ctor(false)");
		System.out.println(db.equals(l));
		System.out.println(l.equals(db));
	}
}
