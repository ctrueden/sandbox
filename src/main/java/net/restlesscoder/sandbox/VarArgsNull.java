
package net.restlesscoder.sandbox;

public class VarArgsNull {

	public static void main(final String... args) {
		stuff((Object) null);
		stuff((Object[]) null);
		stuff(null);
	}

	public static void stuff(Object... things) {
		System.out.println(things == null);
	}
}
