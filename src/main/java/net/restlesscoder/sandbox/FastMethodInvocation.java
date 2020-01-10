
package net.restlesscoder.sandbox;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * @see <a href=
 *      "https://forum.image.sc/t/performance-profiling-ways-of-invoking-a-method-dynamically/27471">Performance
 *      profiling ways of invoking a method dynamically</a>
 */
public class FastMethodInvocation {

	public static void accumulate(final List<?> l, final String s, final long[] result) {
		result[0] += l.size() + s.length();
	}

	public interface Consumer3<T, U, V> {
		void accept(T t, U u, V v);
	}
	public interface GenericTyped {
		Type getType();
	}
	public static class GenericConsumer3<T, U, V> implements Consumer3<T, U, V>, GenericTyped {

		private Consumer3<T, U, V> c;
		private Type type;

		public GenericConsumer3(Consumer3<T, U, V> c, Type type) {
			this.c = c;
			this.type = type;
		}
		@Override
		public Type getType() {
			return type;
		}

		@Override
		public void accept(T t, U u, V v) {
			c.accept(t, u, v);
		}
	}

	public static void main(final String... args) throws Throwable {
		// Simple lambda.
		Consumer3<List<?>, String, long[]> lambda = FastMethodInvocation::accumulate;
		
		// Old-school reflection.
		Method m = FastMethodInvocation.class.getMethod("accumulate", List.class, String.class, long[].class);

		// Get the MethodHandle for accumulate method.
		MethodType methodType = MethodType.methodType(void.class, List.class, String.class, long[].class);
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle handle = lookup.findStatic(FastMethodInvocation.class, "accumulate", methodType);

		// OR:
//		handle = lookup.unreflect(m);

		// First method of converting MethodHandle to Consumer3.
		MethodType consumer3MethodType = MethodType.methodType(Consumer3.class);
		Consumer3<List<?>, String, long[]> dynamicLambda =
			(Consumer3<List<?>, String, long[]>) //
			LambdaMetafactory.metafactory(lookup, "accept", //
				consumer3MethodType, methodType.generic(), handle,
				methodType).getTarget().invokeExact();

		// Second method of converting MethodHandle to Consumer3.
		@SuppressWarnings("unchecked")
		final Consumer3<List<?>, String, long[]> dynamicProxy = //
			MethodHandleProxies.asInterfaceInstance(Consumer3.class, handle);

		GenericConsumer3<List<?>, String, long[]> wrappedLambda = new GenericConsumer3<>(lambda, null);
		GenericConsumer3<List<?>, String, long[]> wrappedDynamicLambda = new GenericConsumer3<>(dynamicLambda, null);
		GenericConsumer3<List<?>, String, long[]> wrappedDynamicProxy = new GenericConsumer3<>(dynamicProxy, null);

		System.out.println(Arrays.toString(dynamicLambda.getClass().getGenericInterfaces()));
		System.out.println(Arrays.toString(dynamicProxy.getClass().getGenericInterfaces()));

		Consumer3<List<?>, String, long[]> inner =
			new Consumer3<List<?>, String, long[]>()
			{
				@Override
				public void accept(final List<?> l, final String s, final long[] result) {
					FastMethodInvocation.accumulate(l, s, result);
				}
			};

		// NB: Naive attempt to avoid inlining of constant values.
		final List<?> l = Arrays.asList(1, 2, 3, 4, 5, System.currentTimeMillis());
		final String s = "Hello " + System.currentTimeMillis();
		final long[] result = {0};

		final long iters = 500_000_000;
		final long start = System.currentTimeMillis();
		for (int i=0; i<iters; i++) {
//			result[0] += l.size() + s.length();
//			accumulate(l, s, result);
//			inner.accept(l, s, result);
//			lambda.accept(l, s, result);
//			wrappedDynamicLambda.accept(l, s, result);
//			wrappedLambda.accept(l, s, result);
			dynamicLambda.accept(l, s, result);
//			m.invoke(null, l, s, result);
//			handle.invoke(l, s, result);
//			dynamicProxy.accept(l, s, result);
//			wrappedDynamicProxy.accept(l, s, result);
		}
		final long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
}
