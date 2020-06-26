
package net.restlesscoder.sandbox.imagej;

import java.io.IOException;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ops.Ops;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imglib2.Dimensions;
import net.imglib2.FinalDimensions;
import net.imglib2.histogram.Histogram1d;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * @see <a href="https://forum.image.sc/t/38363/5">ImageJ2 Histogram op is too
 *      simplistic an implementation for a coloured image?</a>
 */
public class RGBImageHistogram {

	public static <T extends RealType<T> & NativeType<T>> void main(final String... args) throws IOException {
		// Create the ImageJ gateway.
		ImageJ ij = new ImageJ();

		// Load an RGB image.
		Dataset dataset = ij.scifio().datasetIO().open("/Users/curtis/data/clown.jpg");

		// NB: Reconstitute the Dataset object's recursive generic parameter.
		//
		// The Dataset class tries to protect the user from difficult generics.
		// However, because ImgLib2's core interfaces use recursive type parameters
		// of the form "T extends Type<T>", using wildcard types results in an
		// incompatible expression: "? extends Type<?>", where the compiler does not
		// know that the two ?s must in fact be the same type.
		//
		// Because the ImageJ Ops and ImgLib2 libraries use the most specific and
		// correct types for arguments, including generics, we need to go from
		// Dataset, which is an "Img<RealType<?>>" and therefore not a matching
		// type for any of our image processing methods, to Img<T> where
		// "T extends RealType<T> & NativeType<T>". We can do it using an unchecked
		// cast, but only from a method with a T type variable declared, as above.
		@SuppressWarnings("unchecked")
		Img<T> img = (Img<T>) dataset;

		// Find a stats.sum computer op which sums into a DoubleType result.
		// While we could instead use the input type for the result e.g. via
		// "outType = img.firstElement()", this would fail spectacularly for
		// common types like UnsignedByteType, because the sum would quickly
		// overflow, whereas DoubleType is a wide-enough type to accommodate
		// not-super-large images of both integer and floating point types.
		DoubleType outType = new DoubleType();
		UnaryComputerOp<Iterable<T>, DoubleType> sumOp = Computers.unary(ij.op(), Ops.Stats.Sum.class, outType, img);

		// Project the image's third dimension using the sum op into a
		// destination DoubleType image. This assumes our input image is 3D.
		Dimensions dims = new FinalDimensions(img.dimension(0), img.dimension(1));
		Img<DoubleType> projected = ij.op().create().img(dims, outType);
		ij.op().transform().project(projected, img, sumOp, 2);

		// Compute the histogram on the projected image.
		Histogram1d<DoubleType> histogram = ij.op().image().histogram(projected);

		// Output the histogram.
		System.out.println(histogram);
		System.out.println(histogram.distributionCount());
		System.out.println(histogram.valueCount());
		for (int b=0; b<histogram.getBinCount(); b++) {
			System.out.printf("[%d] %d\n", b, histogram.frequency(b));
		}

		// Clean up.
		ij.context().dispose();
	}

}
