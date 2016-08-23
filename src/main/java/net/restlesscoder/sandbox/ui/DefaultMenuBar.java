
package net.restlesscoder.sandbox.ui;

import java.awt.Button;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class DefaultMenuBar {

	public static void main(final String... args) {
		setupDefaultMenuBar();

		final Frame f = new Frame("Hello");
		f.add(new Button("Go"));
//		final JFrame f = new JFrame("Hello");
//		f.getContentPane().add(new JButton("Go"));

		final MenuBar menubar = new MenuBar();
		final Menu file = new Menu("File");
		final MenuItem fileExit = new MenuItem("Exit");
		file.add(fileExit);
		menubar.add(file);
//		f.setMenuBar(menubar);

		f.pack();
		f.setVisible(true);
		f.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				f.dispose();
			}
		});
	}

	private static void setupDefaultMenuBar() {
		final JMenuBar menubar = new JMenuBar();
		final JMenu file = new JMenu("File");
		final JMenuItem fileExit = new JMenuItem("ROCK");
		file.add(fileExit);
		menubar.add(file);
		// NB: Only works on OS X
//		com.apple.eawt.Application.getApplication().setDefaultMenuBar(menubar);
	}

}
