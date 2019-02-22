
package net.restlesscoder.sandbox.imagej;

import com.google.common.math.Quantiles;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;

import org.scijava.util.ArrayUtils;
import org.scijava.util.DoubleArray;

/** Computes the median of an ImgLib2 image using Guava Quantiles. */
public class ImgLib2MedianWithGuava {

	public static void main(final String... args) {
		// Set up some test data.
		final double[] values = { //
			8, 2, 1, 7, 8, //
			3, 6, 1, 6, 0, //
			9, 1, 6, 2, 2 //
		};
		final double[] sorted = new double[values.length];
		System.arraycopy(values, 0, sorted, 0, sorted.length);
		Arrays.sort(sorted);
		System.out.println("values = " + Arrays.toString(values));
		System.out.println("sorted = " + Arrays.toString(sorted));

		// With SciJava collection.
		final Collection<Double> numbers = new DoubleArray(values);
		final double median = Quantiles.median().compute(numbers);
		System.out.println("numbers = " + numbers);
		System.out.println("median = " + median);

		final Img<DoubleType> image = ArrayImgs.doubles(values, 3, 5);

		// With ImgLib2 IterableInterval.
		final IIAsFlatCollection<DoubleType> c = new IIAsFlatCollection<>(image);
		final double medianII = Quantiles.median().compute(c);
		System.out.println("median from II = " + medianII);

		// With RandomAccessibleInterval.
		final RAIAsFlatList<DoubleType> list = new RAIAsFlatList<>(image);
		final double medianRAI = Quantiles.median().compute(list);
		System.out.println("median from RAI = " + medianRAI);
	}

	// -- Helper classes --

	public static class IIAsFlatCollection<T extends RealType<T>> extends
		AbstractCollection<Double>
	{

		private final IterableInterval<T> ii;

		public IIAsFlatCollection(final IterableInterval<T> ii) {
			this.ii = ii;
		}

		@Override
		public int size() {
			final long size = Intervals.numElements(ii);
			return size == 0 ? 0 : ArrayUtils.safeMultiply32(size);
		}

		@Override
		public Iterator<Double> iterator() {
			return new Iterator<Double>() {

				private final Cursor<T> cursor = ii.cursor();

				@Override
				public boolean hasNext() {
					return cursor.hasNext();
				}

				@Override
				public Double next() {
					return cursor.next().getRealDouble();
				}
			};
		}
	}

	public static class RAIAsFlatList<T extends RealType<T>> extends
		AbstractList<Double>
	{

		private final RandomAccessibleInterval<T> rai;

		private final ThreadLocal<RandomAccess<T>> ra =
			new ThreadLocal<RandomAccess<T>>()
			{

				@Override
				public RandomAccess<T> get() {
					return rai.randomAccess();
				}
			};

		public RAIAsFlatList(final RandomAccessibleInterval<T> rai) {
			this.rai = rai;
		}

		@Override
		public Double get(final int index) {
			final RandomAccess<T> access = ra.get();
			IntervalIndexer.indexToPositionForInterval(index, rai, access);
			return access.get().getRealDouble();
		}

		@Override
		public int size() {
			final long size = Intervals.numElements(rai);
			return size == 0 ? 0 : ArrayUtils.safeMultiply32(size);
		}
	}
}
