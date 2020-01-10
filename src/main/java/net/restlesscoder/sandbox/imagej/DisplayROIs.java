
package net.restlesscoder.sandbox.imagej;

import java.util.function.BiConsumer;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.mesh.Mesh;
import net.imagej.mesh.Vertex;
import net.imglib2.Localizable;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccessible;
import net.imglib2.position.FunctionRandomAccessible;
import net.imglib2.roi.Masks;
import net.imglib2.roi.RealMask;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;
import net.imglib2.view.Views;

import visad.Irregular3DSet;
import visad.RealTupleType;
import visad.RealType;
import visad.VisADException;

public class DisplayROIs {

	public static void main(String[] args) throws Throwable {
		ImageJ ij = new ImageJ();

		// Generate a silly binary image.
		BiConsumer<Localizable, BitType> function = (l, t) -> {
			long x = l.getLongPosition(0);
			long y = l.getLongPosition(1);
			long z = l.getLongPosition(2);
			t.set(
				x + y > 50 && x + y < 80 ||
				x + z > 70 && x + z < 110
			);
		};
		FunctionRandomAccessible<BitType> fra = //
			new FunctionRandomAccessible<>(3, function, () -> new BitType());
		long[] min = {0, 0, 0};
		long[] max = {256, 256, 64};
		RandomAccessibleInterval<BitType> image = Views.interval(fra, min, max);
		Dataset dataset = ij.dataset().create(image);
		System.out.println("Dataset created: " + dataset);

		// Compute mesh.
		Mesh mesh = ij.op().geom().marchingCubes(image);
		System.out.println("Mesh computed");

		// Compute convex hull.
		Mesh hull = (Mesh) ij.op().geom().convexHull(mesh).get(0);
		System.out.println("Hull computed");

		// Compute Delaunay triangulation.
		long vertexCount = hull.vertices().size();
		if (vertexCount > Integer.MAX_VALUE) throw new RuntimeException("Hull too large");
		float[][] samples = new float[3][(int) vertexCount];
		int i = 0;
		for (Vertex vertex : hull.vertices()) {
			samples[0][i] = vertex.xf();
			samples[1][i] = vertex.yf();
			samples[2][i] = vertex.zf();
			i++;
		}
		RealType xType = RealType.getRealType("X");
		RealType yType = RealType.getRealType("Y");
		RealType zType = RealType.getRealType("Z");
		RealType[] xyz = {xType, yType, zType};
		RealTupleType xyzType = new RealTupleType(xyz);
		Irregular3DSet set = new Irregular3DSet(xyzType, samples);
		System.out.println("Triangulation computed");

		// Wrap as a RealMask.
		RealMask roi = new RealMask() {
			private ThreadLocal<float[][]> value = new ThreadLocal<float[][]>() {
				@Override
				public float[][] initialValue() {
					return new float[3][1];
				}
			};

			@Override
			public boolean test(RealLocalizable t) {
				float[][] v = value.get();
				try {
					v[0][0] = t.getFloatPosition(0);
					v[1][0] = t.getFloatPosition(1);
					v[2][0] = t.getFloatPosition(2);
					int[] tri = set.valueToTri(v);
					return tri[0] >= 0;
				}
				catch (VisADException exc) {
					throw new RuntimeException(exc);
				}
			}

			@Override
			public int numDimensions() { return 3; }
		};
		System.out.println("ROI created: " + roi);

		class Tester {
			private RealPoint point = new RealPoint(3);
			public void inside(double... p) {
				point.setPosition(p[0], 0);
				point.setPosition(p[1], 1);
				point.setPosition(p[2], 2);
				System.out.println(point + " -> " + roi.test(point));
			}
		}
		Tester t = new Tester();
		System.out.println("Testing some points:");
		t.inside(1, 2, 3);
		t.inside(50, 45, 40);
		t.inside(100, 55, 10);

		// Display the results.
		ij.ui().showUI();
//		ij.get(ROIService.class).add(roi, dataset);
		ij.ui().show("Dataset", dataset);

		// Use ImageJ1 to display the ROI. :-(
//		ImagePlus imp = IJ.getImage();
//		Roi ij1Roi = ij.convert().convert(roi, Roi.class);
//		System.out.println("IJ1 roi = " + ij1Roi);
//		imp.setRoi(ij1Roi);

		// Use BDV to display the ROI. :-/
//		RealRandomAccessible<BoolType> maskRA = Masks.toRealRandomAccessible(roi);
//		BdvFunctions.show(maskRA, dataset, "ROI");

		RealRandomAccessible<BoolType> roiRA = Masks.toRealRandomAccessible(roi);
		RandomAccessible<BoolType> roiRaster = Views.raster(roiRA);
		RandomAccessibleInterval<BoolType> roiRAI = Views.interval(roiRaster, dataset);
		ij.ui().show("ROI", roiRAI);
	}
}
