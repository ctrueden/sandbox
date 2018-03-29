
package net.restlesscoder.sandbox.ui;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SnapshotToBufferedImage {

	public static void main(final String... args) {
		final JPanel p = showFrame();

		final BufferedImage bi = new BufferedImage(p.getWidth(), p.getHeight(),
			BufferedImage.TYPE_INT_ARGB);
		p.paint(bi.getGraphics());

		showSnapshot(bi);
	}

	private static JPanel showFrame() {
		final JFrame f = new JFrame("Stuff");
		final JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(new JButton("NORTH"), BorderLayout.NORTH);
		p.add(new JButton("CENTER"), BorderLayout.CENTER);
		p.add(new JButton("SOUTH"), BorderLayout.SOUTH);
		f.setContentPane(p);
		f.pack();
		f.setLocation(100, 100);
		f.setVisible(true);
		return p;
	}

	private static void showSnapshot(BufferedImage bi) {
		final JFrame f = new JFrame("Snapshot");
		final JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(new JLabel(new ImageIcon(bi)));
		f.setContentPane(p);
		f.pack();
		f.setLocation(500, 100);
		f.setVisible(true);
	}
}
