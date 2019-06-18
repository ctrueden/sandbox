
package net.restlesscoder.sandbox.imagej;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.imagej.ImageJ;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.ImgView;
import net.imglib2.position.FunctionRandomAccessible;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class)
public class GenerativeImage implements Command {

	@Parameter
	private long size = 3000;

	@Parameter(type = ItemIO.OUTPUT)
	private Img<UnsignedByteType> image;

	private static <T extends Type<T>> RandomAccessibleInterval<T> bound(
		final RandomAccessible<T> ra, final long... max)
	{
		final long[] min = new long[max.length];
		return Views.interval(ra, min, max);
	}

	private static <T extends Type<T>> Img<T> wrap(final RandomAccessible<T> ra,
		final long... dims)
	{
		// Bound the function.
		final RandomAccessibleInterval<T> rai = bound(ra, dims);

		// Wrap it as an Img.
		final T t = Util.getTypeFromInterval(rai);
		final ImgFactory<T> factory = new ImgFactory<T>(t) {

			@Override
			public Img<T> create(final long... dimensions) {
				return ImgView.wrap(bound(ra, dimensions), this);
			}

			@Override
			public <S> ImgFactory<S> imgFactory(final S type)
				throws IncompatibleTypeException
			{
				throw new IncompatibleTypeException(type, "Unsupported");
			}

			@Override
			public Img<T> create(final long[] dim, final T type) {
				return create(dim);
			}
		};
		return ImgView.wrap(rai, factory);
	}

	@Override
	public void run() {
		// Define a function in 2-space.
		final int ndim = 2;
		final UnsignedByteType t = new UnsignedByteType();
		final Supplier<UnsignedByteType> typeSupplier = () -> t;
		final BiConsumer<Localizable, UnsignedByteType> triangle = (l, type) -> {
			final long x = l.getLongPosition(0);
			final long y = l.getLongPosition(1);
			final boolean foreground = 2 * x + y < size || 2 * (size - x) + y < size;
			type.setReal(foreground ? 255 : 0);
		};
		final FunctionRandomAccessible<UnsignedByteType> ra = //
			new FunctionRandomAccessible<>(ndim, triangle, typeSupplier);

		// Wrap the function as an Img.
		image = wrap(ra, size - 1, size - 1);
	}

	public static void main(final String... args) throws Exception {
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();
		ij.command().run(GenerativeImage.class, true);
	}
}
