
package net.restlesscoder.sandbox.imagej;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.display.ColorTables;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.RealLUTConverter;
import net.imglib2.display.projector.composite.CompositeXYProjector;
import net.imglib2.display.screenimage.awt.ARGBScreenImage;

public class DisplayImage {

	public static BufferedImage bi(final RandomAccessibleInterval source) {
		return bi(source, 0, 1, 2);
	}

	public static BufferedImage bi(final RandomAccessibleInterval source,
		final int xAxis, final int yAxis, final int cAxis)
	{
		final int w = (int) source.dimension(xAxis);
		final int h = (int) source.dimension(yAxis);
		final int c = (int) source.dimension(cAxis);
		final ARGBScreenImage target = new ARGBScreenImage(w, h);
		final ArrayList<RealLUTConverter> converters =
			new ArrayList<>(c);
		for (int i = 0; i < c; i++) {
			converters.add(new RealLUTConverter(0, 255, ColorTables
				.getDefaultColorTable(i)));
		}
		final CompositeXYProjector proj = new CompositeXYProjector(source, target,
			converters, cAxis);
		proj.setComposite(true);
//		proj.setPosition(new long[] {0, 0, 0});
		proj.map();
		return target.image();
	}

	public static void main(final String... args) throws IOException {
		final ImageJ ij = new ImageJ();

		// load an image of a colorful bird
		final String path = "/Users/curtis/data/toucan.png";
		final Dataset toucan = ij.scifio().datasetIO().open(path);

		final BufferedImage bi = bi(toucan);
		
		final JFrame f = new JFrame("Image");
		f.getContentPane().add(new JLabel(new ImageIcon(bi)));
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}
}
