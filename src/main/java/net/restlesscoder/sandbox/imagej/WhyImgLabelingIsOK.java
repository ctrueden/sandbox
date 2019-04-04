
package net.restlesscoder.sandbox.imagej;

import java.util.Set;

import net.imagej.ImageJ;
import net.imglib2.FinalDimensions;
import net.imglib2.algorithm.labeling.ConnectedComponents.StructuringElement;
import net.imglib2.img.Img;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * The generics of {@link ImgLabeling} always strike me as potentially unsafe.
 * But in practice they are OK. This code explores it.
 */
public class WhyImgLabelingIsOK {

	private static <T, I extends IntegerType<I>> void checkIt(String name,
		ImgLabeling<T, I> imgLabeling, T valueToAdd)
	{
		imgLabeling.firstElement().add(valueToAdd);

		boolean first = true;
		int count = 0;
		for (final LabelingType<T> item : imgLabeling) {
			count++;
			final Set<T> labels = item.getMapping().getLabels(); // A COPY; mutating it will not persist
			//System.out.println(name + ": got item labels: " + labels + " of size " + labels.size());
			for (final T value : labels) {
				if (first) first = false;
				else break;
				System.out.println(name + ": first value = " + value.getClass());
			}
		}
		System.out.println(name + ": item count = " + count + " compared to size = " + imgLabeling.size());
	}

	public static <T> void main(final String... args) throws Exception {
		final ImageJ ij = new ImageJ();

		@SuppressWarnings({ "unchecked", "rawtypes" })
		final Img<UnsignedByteType> img = (Img) ij.scifio().datasetIO().open("/Users/curtis/data/blobs.tif");
		final ImgLabeling<Integer, UnsignedByteType> imgLabelingCCA = ij.op().labeling().cca(img, StructuringElement.EIGHT_CONNECTED);

		final ImgLabeling<String, UnsignedByteType> imgLabelingEmpty = ij.op().create().imgLabeling(new FinalDimensions(3, 4), new UnsignedByteType());

		checkIt("CCA", imgLabelingCCA, 37);
		checkIt("Empty", imgLabelingEmpty, "hello");

		ij.context().dispose();
	}

}
