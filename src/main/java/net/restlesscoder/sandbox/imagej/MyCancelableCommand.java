
package net.restlesscoder.sandbox.imagej;

import net.imagej.ImageJ;

import org.scijava.command.Command;
import org.scijava.command.InteractiveCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class,
	menuPath = "SCF>test IJ2 command>testCancelReason")
public class MyCancelableCommand extends InteractiveCommand {

	@Parameter
	private String name;

	@Override
	public void run() {
		System.err.println("hello, " + name);
		System.err.println("cancelable? " + getInfo().canCancel());
	}

	@Override
	public void cancel(String reason) {
		super.cancel(reason);
		System.out.println(reason);
		if (name != null) System.err.println("script is canceled but name is:" + name);
	}

	@Override
	public void cancel() {
		System.err.println(getCancelReason());
		if (name != null) System.err.println("script is canceled but name is:" + name);
	}

	public static void main(String...strings) {
		ImageJ ij = new ImageJ();
		ij.ui().showUI();
		ij.command().run(MyCancelableCommand.class, true);
	}
}
