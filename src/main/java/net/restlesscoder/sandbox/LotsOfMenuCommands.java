
package net.restlesscoder.sandbox;

import java.awt.Button;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class LotsOfMenuCommands extends Application {

	private static final int ITEM_COUNT = 200;

	public static void main(final String... args) {
		launch(args);
	}

	@Override
	public void start(final Stage stage) {
		stage.setTitle("Stage");
		final Scene scene = new Scene(new VBox(), 400, 350);
		stage.setScene(scene);

		final MenuBar menubar = new MenuBar();
		((VBox) scene.getRoot()).getChildren().addAll(menubar);
		final Menu file = new Menu("File");
		menubar.getMenus().addAll(file);
		for (int i = 0; i < ITEM_COUNT; i++) {
			file.getItems().add(new MenuItem("Item #" + i));
		}

		stage.setX(100);
		stage.setY(450);
		stage.setWidth(300);
		stage.setHeight(300);
		stage.show();

		// also make other kinds of frames
		makeJFrame();
		makeFrame();
	}

	private static void makeJFrame() {
		final JFrame f = new JFrame("JFrame");
		f.getContentPane().add(new JButton("Go"));

		f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		final JMenuBar menubar = new JMenuBar();
		f.setJMenuBar(menubar);
		final JMenu file = new JMenu("File");
		menubar.add(file);
		for (int i = 0; i < ITEM_COUNT; i++) {
			file.add(new JMenuItem("Item #" + i));
		}

		f.setBounds(450, 100, 300, 300);
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

		final java.awt.MenuBar menubar = new java.awt.MenuBar();
		f.setMenuBar(menubar);
		final java.awt.Menu file = new java.awt.Menu("File");
		menubar.add(file);
		for (int i = 0; i < ITEM_COUNT; i++) {
			file.add(new java.awt.MenuItem("Item #" + i));
		}

		f.setBounds(100, 100, 300, 300);
		f.setVisible(true);
	}
}
