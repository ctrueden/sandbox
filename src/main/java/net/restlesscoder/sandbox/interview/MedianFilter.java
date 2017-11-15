
package net.restlesscoder.sandbox.interview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedianFilter {

	private static void medianFilter(double[][] in, double[][] out,
		final int radius)
	{
		final int h = in.length, w = in[0].length;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				out[y][x] = median(in, y, x, radius);
			}
		}
	}

	private static double median(double[][] in, int py, int px, int radius) {
		final int h = in.length, w = in[0].length;
		final List<Double> pts = new ArrayList<>();
		for (int y = py - radius; y <= py + radius; y++) {
			for (int x = px - radius; x <= px + radius; x++) {
				if (y >= 0 && y < h && x >= 0 && x < w) {
					pts.add(in[y][x]);
				}
			}
		}
		Collections.sort(pts);
		return pts.get(pts.size() / 2);
	}

	public static void show(final String title, final double[][] matrix,
		final int radius)
	{
		final int w = matrix[0].length, h = matrix.length;
		final double[][] scaled = new double[h][w];
		medianFilter(matrix, scaled, radius);
		Images.display(title, Images.makeBufferedImage(scaled));
	}

	public static void main(final String... args) {
		final int w = 300, h = 200;
		final double[][] matrix = Images.noise(w, h);

		show("Radius 1", matrix, 1);
		show("Radius 3", matrix, 3);
		show("Radius 7", matrix, 7);
	}
}
