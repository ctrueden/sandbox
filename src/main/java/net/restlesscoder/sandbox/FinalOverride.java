
package net.restlesscoder.sandbox;

/**
 * An illustration that the {@code final} keyword on an interface method
 * argument has no effect.
 */
public class FinalOverride {
	
	public static interface Foo {
		void foo(final int x);
	}
	
	public static class Fubar implements Foo {
		@Override
		public void foo(int x) {
			x *= 2;
			System.out.println("x = " + x);
		}
	}

	public static void main(final String... args) throws Exception {
		new Fubar().foo(10);
	}
}
