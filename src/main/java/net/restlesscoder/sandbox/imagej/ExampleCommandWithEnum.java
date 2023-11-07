
package net.restlesscoder.sandbox.imagej;

import net.imagej.ImageJ;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.module.Module;
import org.scijava.plugin.Parameter;

import java.util.Map;

public class ExampleCommandWithEnum implements Command {

	public enum Fruit {
		APPLE("Cosmic Crisp"),
		ORANGE("Florida Gold"),
		OTHER("Mystery Fruit");

		private final String name;
		Fruit(final String name) {
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
	}

	@Parameter(label = "First fruit")
	private Fruit fruit1;

	@Parameter(label = "Second fruit")
	private Fruit fruit2;

	@Parameter(choices = {"eat", "plant"})
	private String action = "eat";

	@Parameter(type = ItemIO.OUTPUT)
	private String message;

	@Override
	public void run() {
		message = "You " + action + " a " + fruit1 + " and a " + fruit2;
	}

	public static void main(final String... args) throws Exception {
		ImageJ ij = new ImageJ();
		ij.ui().showUI();
		System.out.println("--Running--");
		Module m = ij.command().run(ExampleCommandWithEnum.class, true).get();
		System.out.println("--Outputs--");
		for (Map.Entry<String, Object> entry : m.getOutputs().entrySet()) {
			String s = String.format("%s = %s", entry.getKey(), entry.getValue());
			System.out.println(s);
		}
		System.out.println("--Done--");
	}

}
