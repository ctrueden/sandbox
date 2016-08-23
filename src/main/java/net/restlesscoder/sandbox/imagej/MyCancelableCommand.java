
package net.restlesscoder.sandbox.imagej;

import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;

@Plugin(type = Command.class,
	menuPath = "SCF>test IJ2 command>testCancelReason")
public class MyCancelableCommand extends DynamicCommand {

	@Parameter
	ImagePlus imp;

	@Override
	public void run() {
		IJ.log("hello, image name is:" + imp.getTitle());
	}

	@Override
	public void cancel() {
		IJ.log(getCancelReason());
		if (imp != null) IJ.log("script is canceled but image name is:" + imp
			.getTitle());

	}

}
