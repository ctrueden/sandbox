
package net.restlesscoder.sandbox;

/** Can you narrow the types of exceptions thrown in a subclass? Yes. */
public class ThrowException {

	public static class FooBase {
		public void go() throws Exception {}
	}

	public static class MyException extends Exception { }

	public static class FooExtension extends FooBase {
		@Override
		public void go() throws MyException { }
	}

	public static class FooSubExtension extends FooExtension {
		@Override
		public void go() { }
	}

	public static void main(final String... args) {
		FooExtension fooExt = new FooSubExtension();
		// Unhandled exception type ThrowException.MyException
		//fooExt.go();
	}
}
