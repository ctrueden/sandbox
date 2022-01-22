
package net.restlesscoder.sandbox.imagej;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.imagej.Dataset;
import net.imagej.DefaultDataset;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.axis.CalibratedAxis;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgView;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

public class ReorganizeDims {

	private static <T extends Type<T>> ImgPlus<T> reorganize(ImgPlus<T> image, int[] new2old) {
		int numDims = image.numDimensions();

		if (new2old.length != numDims) {
			throw new IllegalArgumentException("Mismatched dimension count: " + //
				new2old.length + " != " + numDims);
		}

		// Copy dimensional axes into the new order.
		CalibratedAxis[] axes = new CalibratedAxis[image.numDimensions()];
		for (int noo = 0; noo < numDims; noo++) {
			int old = new2old[noo];
			axes[noo] = image.axis(old);
		}

		// Repeatedly permute the image dimensions into shape.
		RandomAccessibleInterval<T> rai = image.getImg();
		for (int noo = 0; noo < numDims; noo++) {
			int old = new2old[noo];
			if (old == noo) continue;
			rai = Views.permute(rai, old, noo);

			// Update index mapping accordingly. This is hairy -- be careful. ;-)
		  for (int i=0; i<numDims; i++) {
		  	if (new2old[i] == noo) {
		  		new2old[i] = old;
		  		break;
		  	}
		  }
		  new2old[noo] = noo;
		}

		// Wrap up the result into a new image.
		return new ImgPlus<>(ImgView.wrap(rai), image.getName(), axes);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static Dataset reorganize(Dataset dataset, int[] new2old) {
		ImgPlus result = reorganize((ImgPlus) dataset.getImgPlus(), new2old);
		return new DefaultDataset(dataset.context(), result);
	}

	public static int[] prioritize(AxisType[] axisTypes, AxisType[] preferredAxisTypes) {
		int numDims = axisTypes.length;
		int[] new2old = new int[numDims];

		// Set up some helper data structures.
		List<AxisType> axisTypeList = Arrays.asList(axisTypes);
		List<Integer> remaining = new ArrayList<>();
		for (int old = 0; old < numDims; old++) remaining.add(old);
		int noo = 0;

		// Prioritize the preferred axis types.
		for (AxisType axisType : preferredAxisTypes) {
			if (noo >= new2old.length) break;
			int old = axisTypeList.indexOf(axisType);
			if (old < 0) continue;
			new2old[noo++] = old;
			if (!remaining.contains(old)) throw new RuntimeException("Internal bug! " + old + " was already used! " + Arrays.toString(new2old));
			remaining.remove((Object) old);
		}

		// Preserve order of any remaining dimensions.
		for (int i = 0; i < remaining.size(); i++) {
			new2old[noo++] = remaining.get(i);
		}

		// Sanity check that we didn't screw anything up.
		if (noo != numDims) {
			throw new RuntimeException("Internal bug! " + noo + " != " + numDims);
		}

		return new2old;
	}

	private static void verify(String name, Object expected, Object actual) {
		if (!expected.equals(actual)) throw new RuntimeException("Unequal " + name + ": " + expected + " != " + actual);
	}

	public static void main(final String... args) {
		ImageJ ij = new ImageJ();

		AxisType foo = Axes.get("foo");
		AxisType bar = Axes.get("bar");
		AxisType[] axes = {Axes.X, Axes.Y, foo, bar, Axes.CHANNEL, Axes.TIME, Axes.Z};
		long[] dims = {13, 17, 5, 2, 3, 7, 11}; // total size: ~500KB

		Dataset dataset = ij.dataset().create(new UnsignedByteType(), dims, "fabulous7D", axes);
		ImgPlus<UnsignedByteType> imgPlus = dataset.typedImg(new UnsignedByteType());

		// Fill the image with noise.
		Random rng = new Random();
		for (UnsignedByteType t : imgPlus) t.set(rng.nextInt(256));

		AxisType[] scikitImageOrder = { Axes.CHANNEL, Axes.X, Axes.Y, Axes.Z, Axes.TIME };
		int[] new2old = prioritize(axes, scikitImageOrder);

		long[] newDims = new long[dims.length];
		for (int noo = 0; noo < newDims.length; noo++) {
			int old = new2old[noo];
			newDims[noo] = dims[old];
		}

		System.out.println("Original lengths: " + Arrays.toString(dims));
		System.out.println("Permuted lengths: " + Arrays.toString(newDims));

		Dataset result = reorganize(dataset, new2old);

		// Now assert that the two datasets have the same content.
		System.out.println("Validating dataset contents");

		int oc = dataset.dimensionIndex(Axes.CHANNEL);
		int ox = dataset.dimensionIndex(Axes.X);
		int oy = dataset.dimensionIndex(Axes.Y);
		int oz = dataset.dimensionIndex(Axes.Z);
		int ot = dataset.dimensionIndex(Axes.TIME);
		int of = dataset.dimensionIndex(foo);
		int ob = dataset.dimensionIndex(bar);

		int nc = result.dimensionIndex(Axes.CHANNEL);
		int nx = result.dimensionIndex(Axes.X);
		int ny = result.dimensionIndex(Axes.Y);
		int nz = result.dimensionIndex(Axes.Z);
		int nt = result.dimensionIndex(Axes.TIME);
		int nf = result.dimensionIndex(foo);
		int nb = result.dimensionIndex(bar);

		long cLen = dataset.dimension(oc); verify("C", cLen, result.dimension(nc));
		long xLen = dataset.dimension(ox); verify("X", xLen, result.dimension(nx));
		long yLen = dataset.dimension(oy); verify("Y", yLen, result.dimension(ny));
		long zLen = dataset.dimension(oz); verify("Z", zLen, result.dimension(nz));
		long tLen = dataset.dimension(ot); verify("T", tLen, result.dimension(nt));
		long fLen = dataset.dimension(of); verify("F", fLen, result.dimension(nf));
		long bLen = dataset.dimension(ob); verify("B", bLen, result.dimension(nb));

		RandomAccess<?> old = dataset.randomAccess();
		RandomAccess<?> noo = result.randomAccess();

		for (int c=0; c<cLen; c++) {
			old.setPosition(c, oc); noo.setPosition(c, nc);
			for (int x=0; x<xLen; x++) {
				old.setPosition(x, ox); noo.setPosition(x, nx);
				for (int y=0; y<yLen; y++) {
					old.setPosition(y, oy); noo.setPosition(y, ny);
					for (int z=0; z<zLen; z++) {
						old.setPosition(z, oz); noo.setPosition(z, nz);
						for (int t=0; t<tLen; t++) {
							old.setPosition(t, ot); noo.setPosition(t, nt);
							for (int f=0; f<fLen; f++) {
								old.setPosition(f, of); noo.setPosition(f, nf);
								for (int b=0; b<bLen; b++) {
									old.setPosition(b, ob); noo.setPosition(b, nb);
									String sampleName = String.format("C%d X%d Y%d Z%d T%d F%d B%d", c, x, y, z, t, f, b);
									verify(sampleName, old.get(), noo.get());
								}
							}
						}
					}
				}
			}
		}

		System.out.println("All is well!");
	}
}
