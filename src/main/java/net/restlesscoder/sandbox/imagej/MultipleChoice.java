package net.restlesscoder.sandbox.imagej;
/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

import net.imagej.ImageJ;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.ChoiceWidget;

/**
 * Demonstration of multiple choice parameters.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = Command.class,
	menuPath = "Plugins>ImageJ Tutorials>Multiple Choice")
public class MultipleChoice implements Command {

	@Parameter(label = "Favorite word",
		choices = { "The", "quick", "brown", "fox",
			"jumps", "over", "the", "lazy", "dog" })
	private String word;

	@Parameter(label = "Green eggs and ham",
		style = ChoiceWidget.RADIO_BUTTON_HORIZONTAL_STYLE,
		choices = { "Yes", "No", "Maybe" })
	private String eggsHam;

	@Parameter(label = "Rating",
		style = ChoiceWidget.RADIO_BUTTON_VERTICAL_STYLE,
		choices = {
			"Loved",
			"Really liked",
			"Liked",
			"Disliked",
			"Really disliked"
		})
	private String rating;

	@Parameter(type = ItemIO.OUTPUT)
	private String info;

	@Override
	public void run() {
		info = "" + //
			"word = " + word + "\n" + //
			"eggsHam = " + eggsHam + "\n" + //
			"rating = " + rating;
	}

	public static void main(final String... args) throws Exception {
		final ImageJ ij = new ImageJ();
		ij.launch(args);
		ij.command().run(MultipleChoice.class, true);
	}

}