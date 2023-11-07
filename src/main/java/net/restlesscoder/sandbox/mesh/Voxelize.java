
package net.restlesscoder.sandbox.mesh;

import java.io.File;
import java.io.IOException;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.mesh.Mesh;
import net.imagej.mesh.io.ply.PLYMeshIO;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class Voxelize {

	public static void main(final String... args) throws IOException {
		// Replace these paths with your desired ones.
		String inPath = "/home/curtis/data/3d/burkardt/ply/airplane.ply";
		String outPath = "/home/curtis/Desktop/airplane.tif";

		System.out.println("Initializing ImageJ2...");
		final ImageJ ij = new ImageJ();
		final OpService ops = ij.op();

		System.out.println("Opening mesh...");
		//Object mesh = ij.io().open(path);
		Mesh mesh = new PLYMeshIO().open(inPath);

		int w = 128, h = 128, d = 128;
		System.out.println("Voxelizing mesh at " + w + " x " + h + " x " + d + "...");
		RandomAccessibleInterval<BitType> mask = ops.geom().voxelization(mesh, w, h, d);

		// Convert image: [0, 1] -> [0, 255]
		// Because ImageJ2/SCIFIO cannot save BitType images to TIFF. :-(
		System.out.println("Wrapping voxelized mesh to 8-bit...");
		RandomAccessibleInterval<UnsignedByteType> mask8bit = Converters.convert(mask, (in, out) -> out.setReal(in.get() ? 255 : 0), new UnsignedByteType());

		System.out.println("Wrapping to ImageJ2 Dataset...");
		// Convert RAI -> Dataset. Because ImageJ2/SCIFIO needs Dataset, not any old RAI. :-(
		Dataset dataset = ij.dataset().create(mask8bit);

		System.out.println("Saving result to TIFF file: " + outPath);
		File outFile = new File(outPath);
		if (outFile.exists()) outFile.delete();
		ij.scifio().datasetIO().save(dataset, outPath);

		System.out.println("All done! " + String.valueOf(Character.toChars(0x1f36d)));
	}
}
