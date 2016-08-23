
package net.restlesscoder.sandbox.ui;

import java.awt.Button;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class InheritedMenuBar {

	public static void main(final String... args) {
		final Frame f = new Frame("Main Frame");
		f.add(new Button("Go"));
//		final JFrame f = new JFrame("Hello");
//		f.getContentPane().add(new JButton("Go"));

		final MenuBar menubar = new MenuBar();
		final Menu file = new Menu("File");
		final MenuItem fileExit = new MenuItem("Exit");
		file.add(fileExit);
		menubar.add(file);
		f.setMenuBar(menubar);

		f.setSize(400, 100);
		f.setVisible(true);
		f.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				f.dispose();
			}
		});
		
		final Frame f2 = new Frame("Second Frame");
		f2.add(new Button("2nd"));
		f2.setSize(300, 300);
		f2.setVisible(true);
		f2.setResizable(false);
		f2.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				f2.dispose();
			}
		});
	}

}
