
package net.restlesscoder.sandbox.imagej;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class UberFloatType extends FloatType {

	private int uber = 5;
	public int uber() { return uber; }

	public static <T extends RealType<T>> void printRealDoubleValue(final T t) {
		System.out.println(t.getRealDouble());
	}

	public static <T extends NativeType<T>> void printNativeType(final T t) {
		System.out.println(t);
	}

	public static void main(String[] args) {
		final long[] dims = {2, 3};

		final UberFloatType uft = new UberFloatType();
		printRealDoubleValue(uft);
		printNativeType(uft);

		// NB: Does not compile.
//		final Img<UberFloatType> img = 
//			new ArrayImgFactory<UberFloatType>().create(dims, uft);
	}
}
