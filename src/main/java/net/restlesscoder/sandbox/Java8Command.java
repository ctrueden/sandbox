
package net.restlesscoder.sandbox;

import java.util.function.Function;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/** A command that uses Java-8-specific syntax. */
@Plugin(type = Command.class, menuPath = "Sandbox > Java 8 Command")
public class Java8Command implements Command {

	@Parameter
	private String name = System.getProperty("user.name");

	@Parameter
	private String greeting;

	@Override
	public void run() {
		sayHello(name, t -> t.toUpperCase());
	}

	public <T, R> void sayHello(final String n,
		final Function<String, String> transformer)
	{
		greeting = "Hello " + transformer.apply(n);
	}

}
