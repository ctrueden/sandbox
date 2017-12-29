
package net.restlesscoder.sandbox.interview;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class PeaksAndDistance {

	/** Sets points with maximal local value to 1.0, others to 0.0. */
	public static void findPeaks(double[][] in, double[][] out) {
		final int h = in.length, w = in[0].length;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				double v = in[y][x];
				// NB: 8-connected comparison.
				if (valueLarger(in, v, x - 1, y - 1) && //
						valueLarger(in, v, x - 1, y) && //
						valueLarger(in, v, x - 1, y + 1) && //
						valueLarger(in, v, x, y - 1) && //
						valueLarger(in, v, x, y + 1) && //
						valueLarger(in, v, x + 1, y - 1) && //
						valueLarger(in, v, x + 1, y) && //
						valueLarger(in, v, x + 1, y + 1))
				{
					out[y][x] = 1.0; // peak
				}
				else out[y][x] = 0.0; // not a peak
			}
		}
	}

	/**
	 * For each point, sets its value to be distance from the nearest peak.
	 * <p>
	 * The distance formula used here is not Euclidean, but rather
	 * {@code max(dist(px, x), dist(py, y))}.
	 * </p>
	 */
	public static void distance(double[][] in, double[][] out) {
		// Mark all pixels as not-yet-complete.
		for (int i = 0; i < out.length; i++)
			Arrays.fill(out[i], Double.NaN);

		final int h = in.length, w = in[0].length;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				double v = in[y][x];
				// NB: 8-connected comparison.
				if (valueLarger(in, v, x - 1, y - 1) && //
						valueLarger(in, v, x - 1, y) && //
						valueLarger(in, v, x - 1, y + 1) && //
						valueLarger(in, v, x, y - 1) && //
						valueLarger(in, v, x, y + 1) && //
						valueLarger(in, v, x + 1, y - 1) && //
						valueLarger(in, v, x + 1, y) && //
						valueLarger(in, v, x + 1, y + 1))
				{
					// Found a peak!
					assignDistances(out, x, y);
				}
			}
		}
	}

	public static void addBlobs(final double[][] image, final int blobSize,
		final double probability)
	{
		final int h = image.length, w = image[0].length;

		for (int y = 0; y < h - blobSize; y++) {
			for (int x = 0; x < w - blobSize; x++) {
				if (Math.random() < probability) {
					image[y][x] += 2.0;
					for (int j = 0; j < blobSize; j++) {
						for (int i = 0; i < blobSize; i++) {
							image[y + j][x + i] += (double) (i + j) / blobSize;
						}
					}
				}
			}
		}
		Images.normalize(image);
	}

	public static void invert(final double[][] image) {
		final int h = image.length, w = image[0].length;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				image[y][x] = 1.0 - image[y][x];
			}
		}
	}

	private static boolean valueLarger(double[][] image, double v, int x, int y) {
		// Bounds check.
		final int h = image.length, w = image[0].length;
		if (x < 0 || y < 0 || x >= w || y >= h) return true;

		return v >= image[y][x];
	}

	private static void assignDistances(double[][] image, int px, int py) {
		final int h = image.length, w = image[0].length;

		// NB: Use BFS rather than DFS!
		class Coord {
			int x, y; double d;
			Coord(int x, int y, double d) { this.x = x; this.y = y; this.d = d; }
		}
		final Deque<Coord> queue = new ArrayDeque<>();
		queue.add(new Coord(px, py, 0));
		while (!queue.isEmpty()) {
			final Coord c = queue.pop();

			// Bounds check.
			if (c.x < 0 || c.y < 0 || c.x >= w || c.y >= h) continue;

			// Skip if there is a closer peak.
			if (c.d >= image[c.y][c.x]) continue;

			image[c.y][c.x] = c.d;

			// Proceed outward.
			// TODO: Reuse Coord objects to reduce allocations.
			queue.add(new Coord(c.x - 1, c.y - 1, c.d + 1));
			queue.add(new Coord(c.x - 1, c.y, c.d + 1));
			queue.add(new Coord(c.x - 1, c.y + 1, c.d + 1));
			queue.add(new Coord(c.x, c.y - 1, c.d + 1));
			queue.add(new Coord(c.x, c.y + 1, c.d + 1));
			queue.add(new Coord(c.x + 1, c.y - 1, c.d + 1));
			queue.add(new Coord(c.x + 1, c.y, c.d + 1));
			queue.add(new Coord(c.x + 1, c.y + 1, c.d + 1));
		}
	}

	public static void main(final String... args) {
		final int w = 300, h = 200;

		final double[][] noise = Images.noise(w, h);
		addBlobs(noise, 10, 0.05);
		Images.display("Noise", Images.makeBufferedImage(noise));

		final double[][] smoothed = new double[h][w];
		MedianFilter.medianFilter(noise, smoothed, 5);
		Images.display("Smoothed", Images.makeBufferedImage(smoothed));

		final double[][] peaks = new double[h][w];
		findPeaks(smoothed, peaks);
		Images.display("Peaks", Images.makeBufferedImage(peaks));

		final double[][] distance = new double[h][w];
		distance(smoothed, distance);
		Images.normalize(distance);
		invert(distance);
		Images.display("Distance", Images.makeBufferedImage(distance));
	}

}
