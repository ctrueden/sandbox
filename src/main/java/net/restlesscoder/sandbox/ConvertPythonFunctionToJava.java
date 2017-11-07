
package net.restlesscoder.sandbox;

import java.util.Arrays;
import java.util.function.Function;

import org.python.core.Py;
import org.python.core.PyObject;
import org.scijava.Context;
import org.scijava.script.ScriptModule;
import org.scijava.script.ScriptService;

public class ConvertPythonFunctionToJava {

	public static void main(final String... args) throws Throwable {
		final Context ctx = new Context();

		// Define a Python function as a script output.
		final String script = "#@output Object hello\n" + //
			"\n" + //
			"def hello(name):\n" + //
			"\treturn \"Hello, \" + str(name) + \"!\"\n" + //
			"\n";

		// Execute the script.
		final ScriptModule m = ctx.service(ScriptService.class).run("func.py",
			script, false).get();

		// Extract the Python function object.
		final Object hello = m.getOutput("hello");
		final PyObject pyFunc = (PyObject) hello;
		if (!pyFunc.isCallable()) {
			throw new IllegalStateException("expected callable Python object");
		}

		// Convert the Python function to a Java function.
		final Function<Object, Object> func = t -> pyFunc.__call__(
			t instanceof Object[] ? pyArgs((Object[]) t) : pyArgs(t));

		// Try out our shiny new Java function.
		System.out.println(func.apply("Curtis"));
	}

	private static PyObject[] pyArgs(final Object... o) {
		if (o == null) return null;
		return Arrays.stream(o).map(Py::java2py).toArray(PyObject[]::new);
	}
}
