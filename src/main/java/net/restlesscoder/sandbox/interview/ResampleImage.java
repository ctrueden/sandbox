
package net.restlesscoder.sandbox.interview;

public class ResampleImage {

	/** "Nearest" neighbor resampling. */
	public static void resampleNN(final double[][] in, final double[][] out) {
		final int iw = in[0].length, ih = in.length;
		final int ow = out[0].length, oh = out.length;

		for (int oy = 0; oy < oh; oy++) {
			final int iy = ih * oy / oh;
			for (int ox = 0; ox < ow; ox++) {
				final int ix = iw * ox / ow;
				out[oy][ox] = in[iy][ix];
			}
		}
	}

	public static void show(final String title, final double[][] matrix,
		final double scale)
	{
		final int w = matrix[0].length, h = matrix.length;
		final int ow = (int) (w * scale), oh = (int) (h * scale);
		final double[][] scaled = new double[oh][ow];
		resampleNN(matrix, scaled);
		Images.display(title, Images.makeBufferedImage(scaled));
	}

	public static void main(final String... args) {
		final int w = 300, h = 200;
		final double[][] matrix = Images.gradient(w, h);

		show("3.5x", matrix, 3.5);
		show("1.5x", matrix, 1.5);
		show("0.333x", matrix, 0.333);
	}
}
