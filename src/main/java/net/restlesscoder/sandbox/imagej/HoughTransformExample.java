
package net.restlesscoder.sandbox.imagej;

import java.util.Arrays;
import java.util.List;

import net.imagej.ImageJ;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.hough.HoughTransforms;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.position.FunctionRandomAccessible;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.view.Views;

public class HoughTransformExample {

	public static void main(String[] args) throws Throwable {
		ImageJ ij = new ImageJ();
		ij.ui().showUI();

		// I have an image.
		RandomAccessible<UnsignedByteType> function = new FunctionRandomAccessible<>(2, //
			(l, t) -> t.setReal(l.getDoublePosition(0) + l.getDoublePosition(1)), //
			UnsignedByteType::new);
		int width = 150, height = 100;
		Interval interval = FinalInterval.createMinSize(0, 0, width, height);
		RandomAccessibleInterval<UnsignedByteType> image = Views.interval(function, interval);
		
		ij.ui().show(image);

		// Hough line transform, and lines above certain peak threshold.
		long[] votespaceSize = HoughTransforms.getVotespaceSize(image);
		RandomAccessibleInterval<UnsignedIntType> votespace = ArrayImgs.unsignedInts(votespaceSize);
		HoughTransforms.voteLines(image, votespace);
		long threshold = 200;
		List<Point> points = HoughTransforms.pickLinePeaks(votespace, threshold);


		// wrangle points into KNIME columns.
		double[] xValues = points.stream().mapToDouble(pt -> pt.getDoublePosition(0)).toArray();
		double[] yValues = points.stream().mapToDouble(pt -> pt.getDoublePosition(1)).toArray();

		System.out.println(points.size() + " " + points.get(0).numDimensions());
//		System.out.println(Arrays.toString(xValues));
//		System.out.println(Arrays.toString(yValues));
		System.out.println(points);
	}
	
}
