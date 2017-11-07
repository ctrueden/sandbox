
package net.restlesscoder.sandbox.imagej;

import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.Dimensions;
import net.imglib2.FinalDimensions;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

import org.scijava.command.Command;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * From <a href="http://forum.imagej.net/t/5068">Discrepancy between different
 * ways of displaying Views</a>.
 *
 * @author Curtis Rueden
 */
@Plugin(type = Command.class)
public class ImgViewDisplay implements Command {

	@Parameter
	private OpService opService;

	@Parameter
	private DisplayService displayService;

	@Override
	public void run() {

		final int[] center = new int[] { 15, 10 };
		final Dimensions dims = new FinalDimensions(31, 21);

		final Img<BitType> img = opService.create().img(dims, new BitType());

		final RandomAccess<BitType> ra = img.randomAccess();
		ra.setPosition(center);
		ra.get().set(true);

		displayService.createDisplay("original", img);

		final IntervalView<BitType> view = Views.interval(img, new long[] { 5, 5 },
			new long[] { 25, 15 });
		ImageJFunctions.show(view, "view - ImageJFunctions.show()");

		displayService.createDisplay("view - DisplayService.createDisplay()", view);
	}

	public static void main(final String[] args) throws Exception {
		final ImageJ ij = net.imagej.Main.launch(args);
		ij.command().run(ImgViewDisplay.class, false);
	}
}
