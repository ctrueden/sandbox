
package net.restlesscoder.sandbox.imagej;

import java.io.IOException;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BitType;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt.MessageType;
import org.scijava.ui.UIService;

/** Example of how to limit processing to image of a particular kind. */
@Plugin(type = Command.class)
public class CheckImageType implements Command {

	@Parameter
	private UIService uiService;

	@Parameter
	private Dataset dataset;

	@Override
	public void run() {
		if (!(dataset.firstElement() instanceof BooleanType)) {
			uiService.showDialog("Binary image required.", MessageType.ERROR_MESSAGE);
		}
		process((ImgPlus) dataset.getImgPlus());
	}

	private <B extends BooleanType<B>> void process(ImgPlus<B> imgPlus) {
		// Count true vs. false values
		long falses = 0, trues = 0;
		for (final B value : imgPlus) {
			if (value.get()) trues++;
			else falses++;
		}
		final long total = trues + falses;
		final double percent = (double) trues / total;
		uiService.showDialog("Image is " + percent + "% foreground (" + //
			trues + "/" + total + ")", MessageType.INFORMATION_MESSAGE);
	}

	public static void main(final String... args) throws IOException {
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		// load an image of a colorful bird
		final String path = "/Users/curtis/data/toucan.png";
		final Dataset toucan = ij.scifio().datasetIO().open(path);
		ij.ui().show(toucan);

		// Make a binary version of it.
		IterableInterval<BitType> otsu = ij.op().threshold().otsu((Img) toucan);

		ij.command().run(CheckImageType.class, true);
	}
}
