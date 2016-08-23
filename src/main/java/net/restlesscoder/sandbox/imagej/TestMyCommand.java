
package net.restlesscoder.sandbox.imagej;

import net.imagej.ImageJ;
import net.imagej.patcher.LegacyInjector;

import ij.IJ;
import ij.ImagePlus;

public class TestMyCommand {

	static {
		LegacyInjector.preinit();
	}

	public static void main(final String... args) throws Exception {
		// Launch ImageJ as usual.
		final ImageJ ij = net.imagej.Main.launch(args);

		// Show the user interface.
		ij.ui().showUI();

		// Open an image.
		ImagePlus blobs = IJ.openImage("http://imagej.nih.gov/ij/images/blobs.gif");
		blobs.show();
		// Alternately, use ImageJ2:
//		Object blobs = ij.io().open("http://imagej.nih.gov/ij/images/blobs.gif");
//		ij.ui().show(blobs);

		// Launch the command.
		ij.command().run(MyCancelableCommand.class, true);
	}

}
