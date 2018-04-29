
package net.restlesscoder.sandbox;

import java.net.URL;
import java.net.URLClassLoader;

public class LoadClassFromByteArray {

	public static void main(final String... args) throws Exception {
//		public class Hello {
//		  public static String greet() {
//		    return "Greetings";
//		  }
//		}
		final byte[] bytecode = { //
			(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe, //
			0x00, 0x00, 0x00, 0x34, 0x00, 0x11, 0x0a, 0x00, 0x04, 0x00, 0x0d, 0x08,
			0x00, 0x0e, 0x07, 0x00, 0x0f, 0x07, 0x00, 0x10, 0x01, 0x00, 0x06, 0x3c,
			0x69, 0x6e, 0x69, 0x74, 0x3e, 0x01, 0x00, 0x03, 0x28, 0x29, 0x56, 0x01,
			0x00, 0x04, 0x43, 0x6f, 0x64, 0x65, 0x01, 0x00, 0x0f, 0x4c, 0x69, 0x6e,
			0x65, 0x4e, 0x75, 0x6d, 0x62, 0x65, 0x72, 0x54, 0x61, 0x62, 0x6c, 0x65,
			0x01, 0x00, 0x05, 0x67, 0x72, 0x65, 0x65, 0x74, 0x01, 0x00, 0x14, 0x28,
			0x29, 0x4c, 0x6a, 0x61, 0x76, 0x61, 0x2f, 0x6c, 0x61, 0x6e, 0x67, 0x2f,
			0x53, 0x74, 0x72, 0x69, 0x6e, 0x67, 0x3b, 0x01, 0x00, 0x0a, 0x53, 0x6f,
			0x75, 0x72, 0x63, 0x65, 0x46, 0x69, 0x6c, 0x65, 0x01, 0x00, 0x0a, 0x48,
			0x65, 0x6c, 0x6c, 0x6f, 0x2e, 0x6a, 0x61, 0x76, 0x61, 0x0c, 0x00, 0x05,
			0x00, 0x06, 0x01, 0x00, 0x09, 0x47, 0x72, 0x65, 0x65, 0x74, 0x69, 0x6e,
			0x67, 0x73, 0x01, 0x00, 0x05, 0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x01, 0x00,
			0x10, 0x6a, 0x61, 0x76, 0x61, 0x2f, 0x6c, 0x61, 0x6e, 0x67, 0x2f, 0x4f,
			0x62, 0x6a, 0x65, 0x63, 0x74, 0x00, 0x21, 0x00, 0x03, 0x00, 0x04, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x01, 0x00, 0x05, 0x00, 0x06, 0x00,
			0x01, 0x00, 0x07, 0x00, 0x00, 0x00, 0x1d, 0x00, 0x01, 0x00, 0x01, 0x00,
			0x00, 0x00, 0x05, 0x2a, (byte) 0xb7, 0x00, 0x01, (byte) 0xb1, 0x00, 0x00,
			0x00, 0x01, 0x00, 0x08, 0x00, 0x00, 0x00, 0x06, 0x00, 0x01, 0x00, 0x00,
			0x00, 0x01, 0x00, 0x09, 0x00, 0x09, 0x00, 0x0a, 0x00, 0x01, 0x00, 0x07,
			0x00, 0x00, 0x00, 0x1b, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03,
			0x12, 0x02, (byte) 0xb0, 0x00, 0x00, 0x00, 0x01, 0x00, 0x08, 0x00, 0x00,
			0x00, 0x06, 0x00, 0x01, 0x00, 0x00, 0x00, 0x03, 0x00, 0x01, 0x00, 0x0b,
			0x00, 0x00, 0x00, 0x02, 0x00, 0x0c };

		Class<?> c;
		try (final BytesClassLoader cl = new BytesClassLoader()) {
			c = cl.loadClass("Hello", bytecode);
		}
		final Object result = c.getMethod("greet").invoke(null);
		System.out.println(result);
	}

	private static class BytesClassLoader extends URLClassLoader {

		public BytesClassLoader() {
			super(new URL[0]);
		}

		public Class<?> loadClass(final String name, final byte[] bytes) {
			return defineClass(name, bytes, 0, bytes.length);
		}
	}
}
