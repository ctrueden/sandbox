
package net.restlesscoder.sandbox.imagej;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.roi.DefaultROITree;
import net.imagej.roi.ROITree;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.IterableRegion;
import net.imglib2.roi.MaskPredicate;
import net.imglib2.roi.Masks;
import net.imglib2.roi.RealMaskRealInterval;
import net.imglib2.roi.Regions;
import net.imglib2.roi.geom.GeomMasks;
import net.imglib2.roi.geom.real.WritableEllipsoid;
import net.imglib2.roi.geom.real.WritablePolygon2D;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Intervals;
import net.imglib2.view.IntervalView;
import net.imglib2.view.RandomAccessibleOnRealRandomAccessible;
import net.imglib2.view.Views;

import bdv.util.BdvFunctions;
import bdv.util.BdvOptions;

public class IterateImageOverROI {

	public static void main(final String... args) throws IOException {
		// Create some ImgLib2 ROIs.
		final WritableEllipsoid ellipsoid = GeomMasks.closedEllipsoid(new double[] {5, 6}, new double[] {7, 8});
		final WritablePolygon2D polygon = GeomMasks.polygon2D(new double[] {2, 3, 4, 5, 6, 1, 9, 9}, new double[] {7, 5, 3, 4, 6, 1, 1, 9});
		final List<MaskPredicate<?>> rois = Arrays.asList(ellipsoid, polygon);

		// Lump them into a collection.
		final ROITree roiTree = new DefaultROITree();
		roiTree.addROIs(rois);

		showROI(polygon);

		ImageJ ij = new net.imagej.ImageJ();
		Dataset clown = ij.scifio().datasetIO().open("/Users/curtis/data/clown.jpg");
		final IntervalView<UnsignedByteType> clown8 = Views.hyperSlice(clown.typedImg(new UnsignedByteType()), 2, 0);

		// Convert ROI from R^n to Z^n.
		final RandomAccessible<BoolType> discreteROI = //
			Views.raster(Masks.toRealRandomAccessible(polygon));

		// Apply finite bounds to the discrete ROI.
		final IntervalView<BoolType> boundedDiscreteROI = //
			Views.interval(discreteROI, clown8);

		// Create an iterablev ersion of the finite discrete ROI.
		IterableRegion<BoolType> iterableROI = //
			Regions.iterable(boundedDiscreteROI);

		// Make an iterable image over only the samples contained in the ROI.
		final IterableInterval<UnsignedByteType> iterableImageOnROI = //
			Regions.sample(iterableROI, clown8);

		// Do something to each sample within the ROI.
		iterableImageOnROI.forEach(t -> t.set(255));

		ij.ui().showUI();
		ij.ui().show(clown8);
	}

	public static void showROI(final RealMaskRealInterval roi) {
		final RealRandomAccessibleRealInterval<BoolType> rrari =
				Masks.toRealRandomAccessibleRealInterval(roi);

		RandomAccessibleOnRealRandomAccessible<BoolType> ra = Views.raster(rrari);

		Interval bounds = Intervals.smallestContainingInterval(rrari);
		IntervalView<BoolType> rai = Views.interval(ra, bounds);

		BdvFunctions.show(rai, "Awesome", new BdvOptions().is2D());
	}
}
