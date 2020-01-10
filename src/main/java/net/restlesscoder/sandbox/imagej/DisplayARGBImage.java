
package net.restlesscoder.sandbox.imagej;

import java.util.Arrays;
import java.util.List;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.axis.Axes;
import net.imagej.axis.IdentityAxis;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

public class DisplayARGBImage {

	public static void main(String[] args) throws Exception {
		ImageJ ij = new ImageJ();
		ij.ui().showUI();

		// A nice ARGB image.
		Img<ARGBType> argbImage = ArrayImgs.argbs(512, 384);
		Cursor<ARGBType> c = argbImage.cursor();
		for (int y = 0; y < argbImage.dimension(1); y++) {
			for (int x = 0; x < argbImage.dimension(0); x++) {
				ARGBType pixel = c.next();
				int r = (x + y) % 255;
				int g = (x * y) % 255;
				int b = (y - x) % 255;
				int a = 255;
				pixel.set(ARGBType.rgba(r, g, b, a));
			}
		}
		// The ARGB image is not showable:
		//		ij.ui().show(argbImage);
		ImageJFunctions.show(argbImage);

		// Internal conversions needed to convert to Dataset.
		// This could (should?) be a SciJava Converter plugin.
		List<RandomAccessibleInterval<UnsignedByteType>> channels = Arrays.asList(
			Converters.argbChannel(argbImage, 1),
			Converters.argbChannel(argbImage, 2),
			Converters.argbChannel(argbImage, 3)
			//			Converters.argbChannel(argbImage, 0)
				);
		Dataset dataset = ij.dataset().create(Views.stack(channels));
		dataset.setAxis(new IdentityAxis(Axes.CHANNEL), dataset.numDimensions() - 1);

		// NB: The following call gives a hint to the ImageJ Legacy layer that
		// this dataset should be collapsed to IJ1's RGB type. The dataset must
		// be uint8 with the third dimension axis of type Channel and length 3,
		// or else an exception will be thrown.
		dataset.setRGBMerged(true);
		ij.ui().show(dataset);

		//		DatasetView datasetView = (DatasetView) ij.imageDisplay().createDataView(dataset);
		//		datasetView.setComposite(true);
		//		ij.ui().show(datasetView);
	}
}
