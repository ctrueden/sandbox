
package net.restlesscoder.sandbox.imagej;

import io.scif.img.ImgOpener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.imagej.ImageJ;
import net.imagej.ops.Op;
import net.imagej.ops.Ops;
import net.imagej.ops.special.function.Functions;
import net.imagej.ops.special.function.UnaryFunctionOp;
import net.imglib2.AbstractInterval;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Sampler;
import net.imglib2.View;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Intervals;
import net.imglib2.view.IntervalView;

public class FuseRAIs {

	public static void main(final String... args) throws Exception {
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		final ImgOpener o = new ImgOpener(ij.context());
		@SuppressWarnings("unchecked")
		final Img<UnsignedByteType> clown = (Img<UnsignedByteType>) o.openImgs(
			"/Users/curtis/data/clown8.tif").get(0);
		final Img<DoubleType> clown64 = ij.op().convert().float64(clown);

		final Img<DoubleType> gradient = ij.op().create().img(
			new long[] { 312, 282 });
		ij.op().image().equation(gradient, "p[0] + p[1]");

		final Img<DoubleType> sinusoid = ij.op().create().img(
			new long[] { 133, 244 });
		ij.op().image().equation(sinusoid, "10 * (Math.cos(0.3*p[0]) + Math.sin(0.3*p[1]))");
		final IntervalView<DoubleType> shiftedSinusoid = //
			ij.op().transform().translateView(sinusoid, 111, -88);

		final List<DoubleType> proto = //
			Arrays.asList(clown64.firstElement(), gradient.firstElement());

		Class<? extends Op> stat = Ops.Stats.Mean.class; // Ops.Stats.Max.class;
		final UnaryFunctionOp<List<DoubleType>, DoubleType> op = //
			Functions.unary(ij.op(), stat, null, proto);

		final RandomAccessibleInterval<DoubleType> fused = //
			fuse(Arrays.asList(gradient, clown64, shiftedSinusoid), op::calculate);

		ij.ui().show("Clown", clown64);
		ij.ui().show("Gradient", gradient);
		ij.ui().show("Sinusoid", sinusoid);
		ij.ui().show("Fusion!", fused);
		ImageJFunctions.show(fused, "Fusion shown with IJ1");
	}

	// -- Utility methods --

	public static <T> Interval union(
		final List<? extends RandomAccessibleInterval<T>> intervals)
	{
		if (intervals == null || intervals.size() == 0) {
			throw new IllegalArgumentException("Need at least one image");
		}
		final int n = intervals.get(0).numDimensions();
		final long[] min = new long[n];
		final long[] max = new long[n];
		Arrays.fill(min, Long.MAX_VALUE);
		Arrays.fill(max, Long.MIN_VALUE);
		for (int i = 0; i < intervals.size(); i++) {
			if (intervals.get(i).numDimensions() != n)
				throw new IllegalArgumentException("Mismatched dimension count");
			for (int d = 0; d < n; ++d) {
				final long mn = intervals.get(i).min(d);
				if (mn < min[d]) min[d] = mn;
				final long mx = intervals.get(i).max(d);
				if (mx > max[d]) max[d] = mx;
			}
		}
		return new FinalInterval(min, max);
	}

	public static <T extends RealType<T>> RandomAccessibleInterval<T> fuse(
		final List<RandomAccessibleInterval<T>> images,
		final Function<List<T>, T> fuser)
	{
		return new FusedView<>(images, fuser);
	}

	// -- Helper classes --

	public static class FusedView<T> extends AbstractInterval implements
		RandomAccessibleInterval<T>, View
	{

		private final List<? extends RandomAccessibleInterval<T>> images;
		private final Function<List<T>, T> fuser;

		public FusedView(final List<RandomAccessibleInterval<T>> images,
			final Function<List<T>, T> fuser)
		{
			super(union(images));
			this.images = images;
			this.fuser = fuser;
		}

		@Override
		public RandomAccess<T> randomAccess() {
			return new FusedRandomAccess();
		}

		@Override
		public RandomAccess<T> randomAccess(final Interval interval) {
			return randomAccess(); // FIXME
		}

		private class FusedRandomAccess extends Point implements RandomAccess<T> {

			private final List<RandomAccess<T>> ras;
			private final ArrayList<T> samples;

			public FusedRandomAccess() {
				super(images.get(0).numDimensions());
				ras = images.stream() //
					.map(image -> image.randomAccess()) //
					.collect(Collectors.toList());
				samples = new ArrayList<>(images.size());
			}

			@Override
			public T get() {
				samples.clear();
				for (int i = 0; i < images.size(); i++) {
					if (!Intervals.contains(images.get(i), this)) continue;
					ras.get(i).setPosition(this);
					samples.add(ras.get(i).get());
				}
				return fuser.apply(samples);
			}

			@Override
			public Sampler<T> copy() {
				throw new UnsupportedOperationException();
			}

			@Override
			public RandomAccess<T> copyRandomAccess() {
				throw new UnsupportedOperationException();
			}
		}

	}
}
