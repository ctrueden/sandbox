
package net.restlesscoder.sandbox.scijava;

import javax.swing.JOptionPane;

import org.scijava.MenuPath;
import org.scijava.SciJava;
import org.scijava.command.Command;
import org.scijava.command.CommandInfo;
import org.scijava.menu.ShadowMenu;

/**
 * Adds some commands to the SciJava menu which are backed by {@link Runnable}
 * objects.
 */
public class RunnableCommands {

	public static void main(final String... args) throws Exception {
		// Create a SciJava application context.
		final SciJava sj = new SciJava();

		// Create some simple Runnable objects, as a demo.
		final Runnable hello = new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, "Hello world");
			}
		};
		final Runnable aloha = new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, "Aloha");
			}
		};

		// Create Command wrappers for the Runnable objects, and register them.
		final CommandInfo helloCommand = new RunnableCommandInfo(hello);
		helloCommand.setMenuPath(new MenuPath("Greetings>Hello World"));
		sj.module().addModule(helloCommand);

		final CommandInfo alohaCommand = new RunnableCommandInfo(aloha);
		alohaCommand.setMenuPath(new MenuPath("Greetings>Aloha"));
		sj.module().addModule(alohaCommand);

		// Verify that the SciJava menu has been updated accordingly.
		final ShadowMenu greetingsMenu = sj.menu().getMenu().getMenu("Greetings");
		sj.log().info("greetings menu:\n" + greetingsMenu);

		// Display the UI, to test out the commands!
		sj.ui().showUI();
	}

	public static class RunnableCommandInfo extends CommandInfo {
		private Runnable r;

		public RunnableCommandInfo(final Runnable r) {
			super(RunnableCommand.class);
			this.r = r;
		}
		@Override
		public RunnableCommand createInstance() {
			return new RunnableCommand(r);
		}
	}

	public static class RunnableCommand implements Command {
		private final Runnable r;
		public RunnableCommand(Runnable r) {
			this.r = r;
		}
		@Override
		public void run() {
			r.run();
		}
	}
}
