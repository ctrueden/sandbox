
package net.restlesscoder.sandbox.imagej;

import java.io.IOException;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class ConvertersClamp {

	public static void main(final String... args) throws IOException {
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		// Load an image of a colorful bird.
		final String path = "/Users/curtis/data/toucan.png";
		final Dataset toucan = ij.scifio().datasetIO().open(path);
		final ImgPlus<UnsignedByteType> img = toucan.typedImg(new UnsignedByteType());

		// Make bird less colorful.
		RandomAccessibleInterval<UnsignedByteType> clamped = clamp(img, 50, 100, new UnsignedByteType());
		ij.ui().show("Clamped", clamped);
	}

	public static <T extends RealType<T>> RandomAccessibleInterval<T> clamp(
		final RandomAccessibleInterval<T> image, final double min, final double max, final T type)
	{
		return Converters.convert(image, (in, out) -> {
			double value = in.getRealDouble();
			if (value < min) value = min;
			else if (value > max) value = max;
			out.setReal(value);
		}, type);
	}
}
