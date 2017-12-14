
package net.restlesscoder.sandbox.imagej;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ops.Ops;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imglib2.algorithm.neighborhood.DiamondShape;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.algorithm.neighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;

public class MedianBySlice {

	public static void main(final String... args) throws Exception {
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();
		
		Dataset mriStack = ij.scifio().datasetIO().open("/Users/curtis/data/mri-stack.tif");
		ij.ui().show("Original", mriStack);
		
		final Img<UnsignedByteType> img = mriStack.typedImg(new UnsignedByteType());
		final Img<FloatType> in = ij.op().convert().float32(img);
		final Img<FloatType> out = ij.op().create().img(in);

		ij.ui().show("Rectangle", median(ij, in, out, new RectangleShape(5, false)));
		ij.ui().show("Circle", median(ij, in, out, new HyperSphereShape(5)));
		ij.ui().show("Diamond", median(ij, in, out, new DiamondShape(5)));
	}

	private static Object median(ImageJ ij, Img<FloatType> in, Img<FloatType> out,
		Shape shape)
	{
		UnaryComputerOp op = Computers.unary(ij.op(), Ops.Filter.Median.class, out, in, shape);
		return ij.op().slice(out, in, op, new int[] {0, 1});
	}
}
