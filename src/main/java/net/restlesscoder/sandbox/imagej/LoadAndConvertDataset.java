
package net.restlesscoder.sandbox.imagej;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imagej.ops.Ops;
import net.imagej.ops.special.Computers;
import net.imagej.ops.special.UnaryComputerOp;
import net.imglib2.IterableInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

public class LoadAndConvertDataset {

	public static void main(final String... args) throws IOException {
		final ImageJ ij = new ImageJ();

		// ask the user for a file to open
		final JFileChooser chooser = new JFileChooser();
		final int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) return;
		final File file = chooser.getSelectedFile();

		final Dataset d = ij.scifio().datasetIO().open(file.getAbsolutePath());

		@SuppressWarnings({"rawtypes", "unchecked"})
		final IterableInterval<BitType> oneBit =
			makeBitType(ij, (ImgPlus) d.getImgPlus());

		System.out.println("-- Original --");
		System.out.println(ij.op().image().ascii(d.getImgPlus()));
		ij.ui().show(d);

		System.out.println("-- Bit --");
		System.out.println(ij.op().image().ascii(oneBit));
	}

	private static <T extends RealType<T>> IterableInterval<BitType> makeBitType(
		final ImageJ ij, final IterableInterval<T> input)
	{
		final UnaryComputerOp<T, BitType> bitOp = Computers.unary(ij.op(),
			Ops.Convert.Bit.class, new BitType(), input.firstElement());
		IterableInterval<BitType> result = ij.op().map(input, bitOp, new BitType());
		return result;
	}

}
