
package net.restlesscoder.sandbox;

import java.awt.Button;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

public class LotsOfMenuCommands {

	public static void main(final String... args) {
		makeFrame();
		makeJFrame();
	}

	private static void makeJFrame() {
		final JFrame f = new JFrame("Hello");
		f.getContentPane().add(new JButton("Go"));

		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		final JMenuBar menubar = new JMenuBar();
		f.setJMenuBar(menubar);
		final JMenu file = new JMenu("File");
		menubar.add(file);
		for (int i = 0; i < 40; i++) {
			file.add(new JMenuItem("Item #" + i));
		}

		f.pack();
		f.setVisible(true);
	}

	private static void makeFrame() {
		final Frame f = new Frame("Frame");
		f.add(new Button("Go"));

		f.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				f.dispose();
			}
		});

		final MenuBar menubar = new MenuBar();
		f.setMenuBar(menubar);
		final Menu file = new Menu("File");
		menubar.add(file);
		for (int i = 0; i < 40; i++) {
			file.add(new MenuItem("Item #" + i));
		}

		f.pack();
		f.setVisible(true);
	}

}
