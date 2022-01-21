
package net.restlesscoder.sandbox.imagej;

import net.imagej.Dataset;
import net.imagej.DefaultDataset;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.axis.CalibratedAxis;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgView;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class ShuffleDimOrder {

	public static <T> IntervalView<T> permute(RandomAccessibleInterval<T> rai,
		int fromAxis, int toAxis, CalibratedAxis[] axes)
	{
		// Permute the image dimensions via Views.
		IntervalView<T> permuted = Views.permute(rai, fromAxis, toAxis);

		// Permute the axis metadata in place.
		CalibratedAxis axis = axes[fromAxis];
		axes[fromAxis] = axes[toAxis];
		axes[toAxis] = axis;

		return permuted;
	}

	public static <T> RandomAccessibleInterval<T> promote(
		RandomAccessibleInterval<T> rai, AxisType axisType, int toAxis,
		CalibratedAxis[] axes)
	{
		// Find index of the given axis type.
		int axisIndex = -1;
		for (int i = 0; i < axes.length; i++) {
			if (axes[i].type() == axisType) {
				axisIndex = i;
				break;
			}
		}
		if (axisIndex < 0 || axisIndex == toAxis) return rai;
		return permute(rai, axisIndex, toAxis, axes);
	}

	public static <T extends Type<T>> ImgPlus<T> permuteToPythonStandardOrder(ImgPlus<T> image) {
		// Copy axes into a work array.
		CalibratedAxis[] axes = new CalibratedAxis[image.numDimensions()];
		image.axes(axes);

		// Shuffle the axes into the optimal order.
		int nextAxis = 0;
		AxisType[] axisOrder = {Axes.CHANNEL, Axes.X, Axes.Y, Axes.Z, Axes.TIME};
		RandomAccessibleInterval<T> rai = image.getImg();
		for (AxisType axisType : axisOrder) {
			if (nextAxis == rai.numDimensions()) break; // No more axes to work on.
			RandomAccessibleInterval<T> result = promote(rai, axisType, nextAxis, axes);
			if (result == rai) continue; // No change; keep working on this axis.
			rai = result;
			nextAxis++;
		}

		// Wrap up the result into a new image.
		return new ImgPlus<>(ImgView.wrap(rai), image.getName(), axes);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static Dataset permuteToPythonStandardOrder(Dataset d) {
		ImgPlus result = permuteToPythonStandardOrder((ImgPlus) d.getImgPlus());
		return new DefaultDataset(d.context(), result);
	}

	public static void main(final String... args) {
		ImageJ ij = new ImageJ();

		long[] dims = {13, 17, 3, 5, 2, 7, 11}; // total size: ~500KB

		AxisType foo = Axes.get("foo");
		AxisType bar = Axes.get("bar");
		AxisType[] axes = {Axes.X, Axes.Y, Axes.CHANNEL, foo, bar, Axes.TIME, Axes.Z};

		Dataset d = ij.dataset().create(new UnsignedByteType(), dims, "fabulous7D", axes);

		Dataset result = permuteToPythonStandardOrder(d);

		ij.ui().showUI();
		ij.ui().show("Original", d);
		ij.ui().show("Permuted", result);
	}
}
