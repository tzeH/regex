package de.cheppner.regex;

public class Benchmark {

	interface BenchMethod {
		void operation();
	}

	private static long benchmark(String name, int times, BenchMethod m) {
		for (int i = 0; i < times; ++i)
			m.operation();
		long time = System.nanoTime();
		for (int i = 0; i < times; ++i)
			m.operation();
		time = System.nanoTime() - time;
		System.out.println("  " + name + " took " + toMilli(time) + " ms.");
		return time;
	}

	public static void main(String[] args) {
		int runs = 100000;
//		 String pattern = "(a?)*a*";
//		 String text = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab";
//		 String pattern = "(x+x+)+y";
//		 String text = "xxx";
//		String pattern = "(.+)@(.+)\\.(.+)"; // einfache email
//		String text = "ch@cheppner.de";
//		String pattern = "abcdefghijklmnopqrstuvwxyz";
//		String text = "abcdefghijklmnopqrstuvwxyz";
		String pattern = "(0|(1(01*(00)*0)*1)*)*"; // set of binary numbers that are multiples of 3
		String text = "10101101101"; // 1389
		
		long cheppnerTime;
		long javaTime;

		System.out.println("## " + runs + " iterations of pattern \"" + pattern
				+ "\" ##");

		System.out.println("full cyles (including compilation)");
		cheppnerTime = benchmark("own", runs, () -> runOwn(pattern, text, runs));
		javaTime = benchmark("java", runs, () -> runJava(pattern, text, runs));
		System.out.println("  factor: " + cheppnerTime / (float) javaTime);

		System.out.println("only matching (no compilation)");
		Pattern ownCompiled = Compiler.compile(pattern);
		cheppnerTime = benchmark("own", runs,
				() -> runOwn(ownCompiled, text, runs));
		java.util.regex.Pattern javaCompiled = java.util.regex.Pattern
				.compile(pattern);
		javaTime = benchmark("java", runs,
				() -> runJava(javaCompiled, text, runs));
		System.out.println("  factor: " + cheppnerTime / (float) javaTime);
	}

	private static String toMilli(long l) {
		return "" + l / 1000.0;
	}

	public static void runOwn(String pattern, String text, int times) {
		Compiler.compile(pattern).matches(text);
	}

	public static void runJava(String pattern, String text, int times) {
		java.util.regex.Pattern.compile(pattern).matcher(text).matches();
	}

	public static void runOwn(Pattern pattern, String text, int times) {
		pattern.matches(text);
	}

	public static void runJava(java.util.regex.Pattern pattern, String text,
			int times) {
		pattern.matcher(text).matches();
	}
}
