
package net.restlesscoder.sandbox.imagej;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import net.imglib2.FinalInterval;
import net.imglib2.Localizable;
import net.imglib2.converter.Converter;
import net.imglib2.converter.RealARGBConverter;
import net.imglib2.display.projector.composite.CompositeXYProjector;
import net.imglib2.display.screenimage.awt.ARGBScreenImage;
import net.imglib2.position.FunctionRandomAccessible;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class InteractiveImgLib2 {

	public static void main(final String... args) throws Exception {
		// Define a function in 2-space.
		final int ndim = 2;
		final int[] scale = {10};
		final UnsignedByteType t = new UnsignedByteType();
		final Supplier<UnsignedByteType> typeSupplier = () -> t;
		final BiConsumer<Localizable, UnsignedByteType> function = (l, type) -> {
			final long x = l.getLongPosition(0);
			final long y = l.getLongPosition(1);
			final double result = (Math.cos((double) x / scale[0]) + Math.sin((double) y / scale[0])) / 2;
			type.setReal((int) (255 * result));
		};
		final FunctionRandomAccessible<UnsignedByteType> ra = //
			new FunctionRandomAccessible<>(ndim, function, typeSupplier);

		// Display it.
		final JFrame frame = new JFrame("ImgLib2");
		final JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		frame.setContentPane(contentPane);

		final JPanel canvas = new JPanel() {
			@Override
			public void paint(final Graphics g) {
				final int w = getSize().width;
				final int h = getSize().height;
				final IntervalView<UnsignedByteType> rai = Views.interval(ra, new FinalInterval(w, h));
				final ARGBScreenImage image = new ARGBScreenImage(getSize().width, getSize().height);
				final ArrayList< Converter< UnsignedByteType, ARGBType >> converters = new ArrayList<>();
				final Converter< UnsignedByteType, ARGBType > converter = new RealARGBConverter<>(0, 255);
				converters.add(converter);
				final CompositeXYProjector<UnsignedByteType> proj =
					new CompositeXYProjector<>(rai, image, converters, -1);
				proj.map();
				g.drawImage(image.image(), 0, 0, null);
			}
		};
		contentPane.add(canvas, BorderLayout.CENTER);

		// With interactivity!
		final JScrollBar slider = new JScrollBar(Adjustable.HORIZONTAL, 10, 0, 1, 100);
		slider.addAdjustmentListener(e -> {
			scale[0] = e.getValue();
			canvas.invalidate();
			canvas.repaint();
		});
		contentPane.add(slider, BorderLayout.SOUTH);

		frame.setBounds(100, 100, 500, 500);
		frame.setVisible(true);
	}
}
