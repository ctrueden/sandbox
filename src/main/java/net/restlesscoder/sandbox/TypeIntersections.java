
package net.restlesscoder.sandbox;

import java.util.List;
import java.util.Set;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import org.scijava.Disposable;
import org.scijava.Prioritized;

/**
 * Demonstrates calling a method with type intersections via type variables.
 * Note that it <em>does not work</em> with recursive generics!
 */
public class TypeIntersections {

	public static <T extends Disposable & Prioritized> void simple(T t) {
		t.getPriority(); // Prioritized API
		t.dispose(); // Disposable API
	}

	public static <E, T extends List<E> & Comparable<E>> void generic(T t) {
		t.get(0); // List API
		t.compareTo(null); // Comparable API
	}
	public static <T extends RealType<T> & NativeType<T>> void recursive(T t) {
		t.getRealDouble(); // RealType API
		t.getEntitiesPerPixel(); // NativeType API
	}

	public static void nonGenericIntersection() {
		final Object o = new Object();
		// simple(o); // compile error
		// simple((Disposable) o); // compile error
		simple((Disposable & Prioritized) o); // works
	}

	public static <E, T extends List<E> & Comparable<E>> void genericIntersection() {
		final Object o = new Object();
		// generic(o); // compile error
		generic((List<E> & Comparable<E>) o); // compiler warning, but allowed
	}

	public static <T extends RealType<T> & NativeType<T>> void
		recursiveGenericIntersection()
	{
		final Object o = new Object();
		// recursive(o); // compile error
		// recursive((RealType<T> & NativeType<T>) o); // shockingly, an error!
		recursive((T) o); // compiler warning, but works
	}
}
