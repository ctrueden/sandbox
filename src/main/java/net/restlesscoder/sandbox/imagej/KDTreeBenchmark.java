
package net.restlesscoder.sandbox.imagej;

import net.imglib2.KDTree;
import net.imglib2.RealPoint;

import java.util.*;
import java.util.function.Supplier;

public class KDTreeBenchmark {

	public static void main(final String... args) {
		final Random r = new Random();
		final Map<String, Supplier<RealPoint>> approaches = new LinkedHashMap<>();
		approaches.put("Random", () -> new RealPoint( r.nextDouble(), r.nextDouble(), r.nextDouble() ));
		approaches.put("Constant 1.0", () -> new RealPoint( r.nextDouble(), r.nextDouble(), 1.0 ));
		approaches.put("Constant 0.5", () -> new RealPoint( r.nextDouble(), r.nextDouble(), 0.5 ));
		approaches.put("Constant 0.0", () -> new RealPoint( r.nextDouble(), r.nextDouble(), 0.0 ));
		approaches.put("Perturbed 1.0", () -> new RealPoint( r.nextDouble(), r.nextDouble(), 1.0 + 1e-7*r.nextDouble() ));
		approaches.put("Perturbed 0.5", () -> new RealPoint( r.nextDouble(), r.nextDouble(), 0.5 + 1e-7*r.nextDouble() ));
		approaches.put("Perturbed 0.0", () -> new RealPoint( r.nextDouble(), r.nextDouble(), 1e-7*r.nextDouble() ));
		approaches.put("Perturbed -10.0", () -> new RealPoint( r.nextDouble(), r.nextDouble(), -10 + 1e-7*r.nextDouble() ));

		for (int iter=1; iter<=5; iter++) {
			System.out.println("Iteration #" + iter + ":");
			for (final String approach : approaches.keySet()) {
				final Supplier<RealPoint> supplier = approaches.get(approach);
				final List<RealPoint> points = new ArrayList<>();
				for (int i = 0; i < 100000; i++)
					points.add(supplier.get());

				long start = System.currentTimeMillis();
				new KDTree<>(points, points);
				System.out.println("--> " + approach + ": build tree in " + (System.currentTimeMillis() - start));
			}
		}
	}

}
