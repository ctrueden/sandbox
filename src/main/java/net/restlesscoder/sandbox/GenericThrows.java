
package net.restlesscoder.sandbox;

import java.io.IOException;

public class GenericThrows {

	interface Executable<E extends Exception> {
		void run() throws E;
	}

	public static <E extends Exception> void assertThrows(Class<E> exceptionType, Executable<E> executable) {
		try {
			executable.run();
		}
		catch (Exception exc) {
			if (exceptionType.isInstance(exc)) {
				// assert true!
			}
			else {
				throw new RuntimeException("User passed a lying executable object");
			}
			// assert false!
			exc.printStackTrace();
		}
	}

	public static void main(final String... args) {
		assertThrows(IOException.class, () -> {});
	}
}
