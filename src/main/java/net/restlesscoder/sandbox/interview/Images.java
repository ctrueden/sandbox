
package net.restlesscoder.sandbox.interview;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class Images {

	/** Generates a random noise image, normalized to [0.0, 1.0]. */
	public static double[][] noise(int w, int h) {
		final double[][] matrix = new double[h][w];
		for (int y=0; y<h; y++) {
			for (int x=0; x<w; x++) {
				matrix[y][x] = Math.random();
			}
		}
		return matrix;
	}

	/** Generates a gradient image matrix, normalized to [0.0, 1.0]. */
	public static double[][] gradient(final int w, final int h) {
		final double[][] matrix = new double[h][w];
		final double max = (w - 1) * (w - 1) + (h - 1) * (h - 1);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				matrix[y][x] = (x * x + y * y) / max;
			}
		}
		return matrix;
	}

	/** Normalizes a matrix to [0.0, 1.0]. */
	public static void normalize(final double[][] matrix) {
		final int w = matrix[0].length, h = matrix.length;

		// Find min and max.
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (matrix[y][x] < min) min = matrix[y][x];
				if (matrix[y][x] > max) max = matrix[y][x];
			}
		}

		// Normalize to [0.0, 1.0].
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				matrix[y][x] = (matrix[y][x] - min) / (max - min);
			}
		}
	}

	/** Converts a normalized 2D double matrix to a {@link BufferedImage}. */
	public static BufferedImage makeBufferedImage(final double[][] matrix) {
		final int w = matrix[0].length, h = matrix.length;
		final BufferedImage image = new BufferedImage(w, h,
			BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				final int v = (int) (255 * matrix[y][x]);
				final int rgb = (255 << 24) | (v << 16) | (v << 8) | v;
				image.setRGB(x, y, rgb);
			}
		}
		return image;
	}

	/** Displays the given {@link BufferedImage} in an image window. */
	public static void display(final String title, final BufferedImage image) {
		final JFrame f = new JFrame(title);
		f.getContentPane().add(new JLabel(new ImageIcon(image)));
		f.pack();
		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		f.setVisible(true);
	}
}
