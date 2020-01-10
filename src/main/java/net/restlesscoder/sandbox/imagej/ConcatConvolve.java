
package net.restlesscoder.sandbox.imagej;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

import org.scijava.Context;
import org.scijava.ui.UIService;

public class ConcatConvolve {

	public static void main(String[] args) {
		testCreateAndConvolvePoints();
		System.out.println("SUCCESS");
	}

	/** tests fft based convolve */
	public static void testCreateAndConvolvePoints() {
		Context ctx = new Context();
		OpService ops = ctx.service(OpService.class);
		UIService ui = ctx.service(UIService.class);

		final int xSize = 128;
		final int ySize = 128;
		final int zSize = 128;

		int[] size = new int[] { xSize, ySize, zSize };

		Img<DoubleType> phantom = ops.create().img(size);

		RandomAccess<DoubleType> randomAccess = phantom.randomAccess();

		randomAccess.setPosition(new long[] { xSize / 2, ySize / 2, zSize / 2 });
		randomAccess.get().setReal(255.0);

		randomAccess.setPosition(new long[] { xSize / 4, ySize / 4, zSize / 4 });
		randomAccess.get().setReal(255.0);

		Point location = new Point(phantom.numDimensions());
		location.setPosition(new long[] { 3 * xSize / 4, 3 * ySize / 4, 3 * zSize /
			4 });

		HyperSphere<DoubleType> hyperSphere = new HyperSphere<>(phantom, location,
			5);

		for (DoubleType value : hyperSphere) {
			value.setReal(16);
		}

		// create psf using the gaussian kernel op (alternatively PSF could be an
		// input to the script)
		RandomAccessibleInterval<DoubleType> psf = ops.create().kernelGauss(
			new double[] { 5, 5, 5 }, new DoubleType());


		// convolve psf with phantom
		RandomAccessibleInterval<DoubleType> convolved = ops.create().img(size);
		ops.filter().convolve(convolved, phantom, psf);

//		RandomAccessibleInterval<DoubleType> correlated = ops.create().img(size);
//		ops.filter().correlate(correlated, phantom, psf);
//
//		RandomAccessibleInterval<DoubleType> convolved = ops.create().img(size);
//		ops.run(Ops.Filter.Convolve.class, convolved, phantom, psf);
		
//		ui.showUI();
//		ui.show("'Convolved'", convolved);
//		ui.show("Correlated", correlated);
//		ui.show("Actually convolved", reallyConvolved);
		
		DoubleType sum = new DoubleType();
		DoubleType max = new DoubleType();
		DoubleType min = new DoubleType();

		ops.stats().sum(sum, Views.iterable(convolved));
		ops.stats().max(max, Views.iterable(convolved));
		ops.stats().min(min, Views.iterable(convolved));
		
		System.out.println("sum=" + sum + ", max=" + max + ", min=" + min);

		// GOOD convolve:
		// sum=8750.000184601617, max=3.154534101486206, min=-2.9776862220387557E-7

		// correlate:
		// sum=8750.000170085596, max=3.154534101486206, min=-3.033969164789596E-7

		assertEquals(sum.getRealDouble(), 8750.000184601617, 0.0);
		assertEquals(max.getRealDouble(), 3.154534101486206, 0.0);
		assertEquals(min.getRealDouble(), -2.9776862220387557E-7, 0.0);

		ctx.dispose();
	}

	private static void assertEquals(double e, double a, double tolerance) {
		if (Math.abs(e - a) > tolerance) {
			throw new RuntimeException("failure: e=" + e + ", a=" + a);
		}
	}
}
