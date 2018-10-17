
package net.restlesscoder.sandbox.imagej;

import java.io.File;

import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.roi.boundary.Boundary;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelRegion;
import net.imglib2.roi.labeling.LabelRegions;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

import org.scijava.widget.FileWidget;

/**
 * A more complicated watershed example. See also {@link WaterShed}.
 *
 * @author Curtis Rueden
 */
public class WaterShedInverted {

	public static <T extends RealType<T> & NativeType<T>> void main(final String args[])
		throws Exception
	{
		final ImageJ ij = new net.imagej.ImageJ();
		ij.ui().showUI();

		System.out.println(ij.op().help("watershed"));

		final File file = ij.ui().chooseFile(null, FileWidget.OPEN_STYLE);

		@SuppressWarnings("unchecked")
		final RandomAccessibleInterval<T> image = (RandomAccessibleInterval<T>)
			ij.scifio().datasetIO().open(file.getAbsolutePath());

		// Slice off any extra dimensions.
		RandomAccessibleInterval<T> input = image;
		while (input.numDimensions() > 2) {
			input = Views.hyperSlice(input, 2, 0);
		}
		ij.ui().show("Input", input);

		final int sigma = 10;

		Img<T> inverted = ij.op().create().img(input);
		ij.op().image().invert(inverted, Views.iterable(input));

		final RandomAccessibleInterval<T> blurredImg = ij.op().filter().gauss(
			inverted, sigma);
		ij.ui().show("Blurred", blurredImg);

		// Apply a global auto-threshold to the image.
		final Img<BitType> mask = ij.op().create().img(blurredImg, new BitType());
		ij.op().threshold().otsu(mask, Views.iterable(blurredImg));
		ij.ui().show("Thresholded", mask);

		final boolean useEightConnectivity = false;
		final boolean drawWatersheds = false;
		final double[] watershedSigma = {2, 2, 2}; // HACK: Workaround WatershedBinary bug.

		// Perform a watershed on the entire binary image.
		final ImgLabeling<Integer, IntType> binaryWatershed = ij.op().image()
			.watershed(mask, useEightConnectivity, drawWatersheds, watershedSigma);
		ij.ui().show("Watershed (Binary Whole)", binaryWatershed.getIndexImg());

		// Perform a watershed on the binary image foreground.
		final ImgLabeling<Integer, IntType> binaryMaskedWatershed = ij.op().image()
			.watershed(null, mask, useEightConnectivity, drawWatersheds,
				watershedSigma, mask);
		ij.ui().show("Watershed (Binary Masked)", binaryMaskedWatershed.getIndexImg());

		// Perform a grayscale watershed on the entire image.
		final ImgLabeling<Integer, IntType> grayWatershed = ij.op().image()
			.watershed(blurredImg, useEightConnectivity, drawWatersheds);
		ij.ui().show("Watershed (Gray Whole)", grayWatershed.getIndexImg());

		// Perform a grayscale watershed on the image foreground.
		final ImgLabeling<Integer, IntType> grayMaskedWatershed = ij.op().image()
			.watershed(null, blurredImg, useEightConnectivity, drawWatersheds, mask);
		ij.ui().show("Watershed (Gray Masked)", grayMaskedWatershed.getIndexImg());

		// create the edge image
		final Img<UnsignedByteType> edgesImg = ij.op().create().img(input, new UnsignedByteType());
		final RandomAccess<UnsignedByteType> edgesImgRA = edgesImg.randomAccess();
		final double maxValue = edgesImg.firstElement().getMaxValue();

		// get the regions from the ImgLabeling
		final LabelRegions<Integer> regions = new LabelRegions<>(grayWatershed);

		// paint each edge region onto the edges image
		for (final LabelRegion<Integer> region : regions) {
			final Boundary<BoolType> b = new Boundary<>(region);
			final Cursor<Void> cursor = b.cursor();
			while (cursor.hasNext()) {
				cursor.fwd();
				edgesImgRA.setPosition(cursor.getIntPosition(0), 0);
				edgesImgRA.setPosition(cursor.getIntPosition(1), 1);
				edgesImgRA.get().setReal(maxValue);
			}
		}
		ij.ui().show("Edges (Grayscale)", edgesImg);

		// stack the centers on the input image
		// NB: Views.stack requires all images to be the same type.
		// We convert the input to uint8 here, even though it could be lossy.
		// An alternative would be to convert the edgesImg to the T of the input.
		final Img<UnsignedByteType> input8 = ij.op().convert().uint8(Views.iterable(input));
		final RandomAccessibleInterval<UnsignedByteType> stack = Views.stack(input8, edgesImg);

		// TODO place in table next to original
		ij.ui().show("Stack", stack);
	}
}
