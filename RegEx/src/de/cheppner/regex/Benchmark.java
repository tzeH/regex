package de.cheppner.regex;

public class Benchmark {

	interface BenchMethod {
		void operation();
	}

	private static long benchmark(int times, BenchMethod m) {
		// Warmup
		for (int i = 0; i < times / 10; ++i)
			m.operation();

		long time = System.nanoTime();
		for (int i = 0; i < times; ++i)
			m.operation();
		time = System.nanoTime() - time;
		return time;
	}

	private static void compareBench(String name1, String name2, int times,
			BenchMethod m1, BenchMethod m2) {
		long time1 = 0;
		long time2 = 0;

		// Warmup
		long warmTime = 5000 + System.currentTimeMillis();
		long warmTimeResult = 5000 + System.currentTimeMillis();
		while (warmTime > System.currentTimeMillis()) {
			warmTimeResult += benchmark(times / 50, m1);
			warmTimeResult += benchmark(times / 50, m2);
		}

		// Interleave to get more robust results
		for (int i = 0; i < 500; ++i) {
			time1 += benchmark(times / 500, m1);
			time2 += benchmark(times / 500, m2);
		}

		float factor = time1 / (float) time2;

		// System.out.println("  " + name1 + " took " + toMilli(time1) +
		// " ms.");
		// System.out.println("  " + name2 + " took " + toMilli(time2) +
		// " ms.");
		String winner;
		if (Math.abs(factor - 1) < 0.1)
			winner = "nobody wins by more than 10%.";
		else
			winner = (factor < 1 ? name1 : name2) + " wins!";

		System.out.println("  " + factor + " " + winner + " / "
				+ warmTimeResult);
	}

	public static void main(String[] args) {
		benchmark("(a?)*a*", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");
		benchmark("(x+x+)+y", "xxx");
		benchmark("(.+)@(.+)\\.(.+)", "ch@cheppner.de");
		benchmark("abcdefghijklmnopqrstuvwxyz", "abcdefghijklmnopqrstuvwxyz");
		// set of binary numbers that are multiples of 3
		benchmark("(0|(1(01*(00)*0)*1)*)*", "10101101101"); // 1389
	}

	public static void benchmark(final String pattern, final String text) {
		int runs = 100000;

		Pattern ownCompiled = Compiler.compile(pattern);
		java.util.regex.Pattern javaCompiled = java.util.regex.Pattern
				.compile(pattern);

		System.out.println("## " + runs + " iterations of pattern \"" + pattern
				+ "\" ##");

		System.out.println("set vs lists matching (no compilation)");
		compareBench("list", "java", runs, //
				() -> ownCompiled.matcher(text).matches(), //
				() -> javaCompiled.matcher(text).matches());
	}
}
