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
		benchmark(times / 10, m1);
		benchmark(times / 10, m2);

		// Interleave to get more robust results
		for (int i = 0; i < 50; ++i) {
			time1 += benchmark(times / 50, m1);
			time2 += benchmark(times / 50, m2);
		}

		System.out.println("  " + name1 + " took " + toMilli(time1) + " ms.");
		System.out.println("  " + name2 + " took " + toMilli(time2) + " ms.");
		System.out.println("  factor: " + time1 / (float) time2);
	}

	public static void main(String[] args) {
		int runs = 100000;
		// String pattern = "(a?)*a*";
		// String text = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab";
		// String pattern = "(x+x+)+y";
		// String text = "xxx";
		// String pattern = "(.+)@(.+)\\.(.+)"; // einfache email
		// String text = "ch@cheppner.de";
		// String pattern = "abcdefghijklmnopqrstuvwxyz";
		// String text = "abcdefghijklmnopqrstuvwxyz";

		// set of binary numbers that are multiples of 3
		String pattern = "(0|(1(01*(00)*0)*1)*)*";
		String text = "10101101101"; // 1389

		System.out.println("## " + runs + " iterations of pattern \"" + pattern
				+ "\" ##");

		System.out.println("full cyles (including compilation)");
		compareBench("own", "java", runs, //
				() -> Compiler.compile(pattern).matcher(text).matches(), //
				() -> java.util.regex.Pattern.compile(pattern).matcher(text)
						.matches());

		System.out.println("only matching (no compilation)");
		Pattern ownCompiled = Compiler.compile(pattern);
		java.util.regex.Pattern javaCompiled = java.util.regex.Pattern
				.compile(pattern);
		compareBench("own", "java", runs,//
				() -> ownCompiled.matcher(text).matches(),//
				() -> javaCompiled.matcher(text).matches());

		System.out.println("set vs lists matching (no compilation)");
		compareBench("list", "set", runs, //
				() -> ownCompiled.matcher(text).matches(), //
				() -> ownCompiled.setMatcher(text).matches());
	}

	private static String toMilli(long l) {
		return "" + l / 1000.0;
	}
}
