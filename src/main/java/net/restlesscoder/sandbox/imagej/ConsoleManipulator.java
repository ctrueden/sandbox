
package net.restlesscoder.sandbox.imagej;

import java.awt.Component;
import java.awt.Window;

import javax.swing.SwingUtilities;

import net.imagej.ImageJ;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.ui.UIService;
import org.scijava.ui.console.ConsolePane;

public class ConsoleManipulator implements Command {

	@Parameter
	private UIService uiService;

	@Override
	public void run() {
		final ConsolePane<?> consolePane = uiService.getDefaultUI().getConsolePane();
		final Object component = consolePane.getComponent();
		if (component instanceof Component) {
			final Window window = SwingUtilities.getWindowAncestor((Component) component);
			window.setBounds(300, 300, 500, 300);
		}
	}

	public static void main(final String... args) throws Exception {
		// Launch ImageJ as usual.
		final ImageJ ij = new ImageJ();
		ij.launch();

		// Show the user interface.
		ij.ui().showUI();

		System.out.println("It's the output stream!");
		System.err.println("It's the error stream!");

		// Launch the command.
		ij.command().run(ConsoleManipulator.class, true);
	}

}
