
package net.restlesscoder.sandbox.imagej;

import net.imagej.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.roi.boundary.Boundary;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelRegion;
import net.imglib2.roi.labeling.LabelRegions;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

/**
 * An example of using the watershed op from ImageJ Ops. Thanks to
 * Balasubramaniyan_twitter on Gitter for the <a href=
 * "https://gitter.im/imglib/imglib2?at=5bc5a503c08b8b3067213ff8">starting
 * point</a>.
 *
 * @author Curtis Rueden
 */
public class WaterShed {

	public static <T extends RealType<T>> void main(final String args[])
		throws Exception
	{
		final ImageJ ij = new net.imagej.ImageJ();
		ij.ui().showUI();

		System.out.println(ij.op().help("watershed"));

		@SuppressWarnings("unchecked")
		final RandomAccessibleInterval<T> image = (RandomAccessibleInterval<T>)
			ij.scifio().datasetIO().open("http://imagej.net/images/blobs.gif");

		final long w = image.dimension(0);
		final long h = image.dimension(1);
		final FinalInterval slice = FinalInterval.createMinSize(0, 0, 0, w, h, 1);

		final RandomAccessibleInterval<T> input = //
			ij.op().transform().crop(image, slice, true);

		ij.ui().show("Input", input);

		final int sigma = 4;

		final RandomAccessibleInterval<T> blurredImg = ij.op().filter().gauss(input, sigma);

		ij.ui().show("Blurred", blurredImg);

		final boolean useEightConnectivity = false;

		final boolean drawWatersheds = false;

		final ImgLabeling<Integer, IntType> labeling = ij.op().image().watershed(blurredImg,
			useEightConnectivity, drawWatersheds);

		// display the indexImg of the ImgLabeling
		final RandomAccessibleInterval<IntType> indexImg = labeling.getIndexImg();

		ij.ui().show("Index", indexImg);

		// create the edge image
		final Img<UnsignedByteType> edgesImg = ij.op().create().img(input, new UnsignedByteType());
		final RandomAccess<UnsignedByteType> edgesImgRA = edgesImg.randomAccess();
		final double maxValue = edgesImg.firstElement().getMaxValue();

		// get the regions from the ImgLabeling
		final LabelRegions<Integer> regions = new LabelRegions<>(labeling);

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

		ij.ui().show("Edges", edgesImg);

		// stack the centers on the input image
		// NB: Views.stack requires all images to be the same type.
		// We convert the input to uint8 here, even though it could be lossy.
		// An alternative would be to convert the edgesImg to the T of the input.
		final Img<UnsignedByteType> input8 = ij.op().convert().uint8(Views.iterable(input));
		final RandomAccessibleInterval<UnsignedByteType> stack = Views.stack(input8, edgesImg);

		// TODO place in table next to original
		ij.ui().show(stack);
	}
}
