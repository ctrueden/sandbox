
package net.restlesscoder.sandbox.imagej;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

public class StripRecursiveGenerics {

	public static class UntypedImg {
		private Img<?> img;
		public <T> UntypedImg(final Img<T> img) {
			this.img = img;
		}

		public Class<?> type() { return img.firstElement().getClass(); }

		@SuppressWarnings("rawtypes")
		public Img img() { return img; }

		public <T> Img<T> img(final Class<T> type) {
			if (!type.isInstance(img.firstElement())) {
				@SuppressWarnings("unchecked")
				final Img<T> tImg = (Img<T>) img;
				return tImg;
			}
			throw new IllegalArgumentException("Img of type " + type().getName() +
				" is not " + type.getName());
		}

		public <T extends RealType<T>> Img<T> imgReal() {
			if (!RealType.class.isInstance(img.firstElement())) {
				@SuppressWarnings("unchecked")
				final Img<T> tImg = (Img<T>) img;
				return tImg;
			}
			throw new IllegalArgumentException("Img of type " + type().getName() +
				" is not a RealType");
		}
		public <T extends RealType<T>> Img<T> imgReal(final Class<T> type) {
			if (!type.isInstance(img.firstElement())) {
				@SuppressWarnings("unchecked")
				final Img<T> tImg = (Img<T>) img;
				return tImg;
			}
			throw new IllegalArgumentException("Img of type " + type().getName() +
				" is not " + type.getName());
		}
	}
	
	public static <T extends RealType<T>> void processRealImage(Img<T> img) {
		System.out.println("real double = " + img.firstElement().getRealDouble());
	}

	public static void main(final String... args) throws Exception {
		Img<DoubleType> doubleImg = ArrayImgs.doubles(new double[] {1.2, 3.4, 5.6, 7.8}, 2, 2);

		UntypedImg uImg = new UntypedImg(doubleImg);
		
		processRealImage(uImg.imgReal(DoubleType.class));
		// NB: Does not compile.
//		processRealImage(uImg.imgReal());
		processRealImage(uImg.imgReal(FloatType.class));
	}

}
