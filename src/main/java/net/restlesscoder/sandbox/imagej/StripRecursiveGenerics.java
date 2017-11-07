
package net.restlesscoder.sandbox.imagej;

import java.math.BigInteger;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class StripRecursiveGenerics {

	public static class Dataset {

		private final Img<?> img;

		public <T extends Type<T>> Dataset(final Img<T> img) {
			this.img = img;
		}

		public <T extends Type<T>> T type() {
			final Object type = img.firstElement();
			if (!(type instanceof RealType)) {
				throw new IllegalArgumentException("Incompatible type: " + //
					type.getClass().getName());
			}
			@SuppressWarnings("unchecked")
			final T t = (T) type;
			return t;
		}

		public <T extends Type<T>> Img<T> img() {
			final Object type = img.firstElement();
			if (!(type instanceof RealType)) {
				throw new IllegalArgumentException("Incompatible type: " + //
					type.getClass().getName());
			}
			@SuppressWarnings("unchecked")
			final Img<T> tImg = (Img<T>) img;
			return tImg;
		}

		public <T extends Type<T>> Img<T> img(final T type) {
			if (!type.getClass().isInstance(img.firstElement())) {
				throw new IllegalArgumentException("Incompatible type: " + //
					type.getClass().getName());
			}
			@SuppressWarnings("unchecked")
			final Img<T> typedImg = (Img<T>) img;
			return typedImg;
		}
	}

	// Step 1
	public static <T extends RealType<T>> void processImage(final Dataset d) {
		// HACK: The T here is bound to RealType, but the img() method only
		// guarantees Type. The fact that this compiles is surprising.
		final Img<T> img = d.img();
		processImage(img);

		// NB: In contrast, the following does _not_ compile!
		//processImage(d.img());
	}

	// Step 2
	public static <T extends RealType<T>> void processImage(final Img<T> img) {
		System.out.println("real double = " + img.firstElement().getRealDouble());
	}


	public static <T extends RealType<T>> void processImage(final Dataset d,
		final T type)
	{
		processImage(d.img(type));
	}

	public static <T extends IntegerType<T>> void go(final Dataset d) {
		// NB: The following code fails at runtime if called from main,
		// because a DoubleType is not an IntegerType.
		Img<T> img = d.img();
		final T t = img.firstElement();
		BigInteger bi = t.getBigInteger();
		System.out.println(bi);
		System.out.println(img);
	}

	public static void main(final String... args) {
		final Img<DoubleType> doubleImg = ArrayImgs.doubles(new double[] { 1.2, 3.4,
			5.6, 7.8 }, 2, 2);

		final Dataset d = new Dataset(doubleImg);

		processImage(d);
		// NB: Does not compile:
		// processImage(d, d.type());
		// processImage(uImg.img());
	}

}
