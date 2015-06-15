package de.cheppner.regex.benchmark;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import de.cheppner.regex.Compiler;
import de.cheppner.regex.Pattern;
import bb.util.Benchmark;

public class BbBenchmark {
	public static void main(String[] args) throws IllegalArgumentException,
			IllegalStateException, Exception {

		String[] data = { "(a?)*a*",
				"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab", // 0
				"(x+x+)+y", "xxx", // 1
				"(.+)@(.+)\\.(.+)", "ch@cheppner.de", // 2
				"abcdefghijklmnopqrstuvwxyz", "abcdefghijklmnopqrstuvwxyz", // 3
				// set of binary numbers that are multiples of 3 (1389)
				"(0|(1(01*(00)*0)*1)*)*", "10101101101" }; // 4

		System.out.println("Available Data:");
		for (int i = 0; i < data.length / 2; ++i)
			System.out.println(i + ": " + data[2 * i] + " \tData: "
					+ data[2 * i + 1]);

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));

		System.out.print("Enter the number to benchmark: ");
		int chosenData = Integer.parseInt(reader.readLine());
		String pattern = data[2 * chosenData];
		String text = data[2 * chosenData + 1];

		Map<String, Callable<Boolean>> implementations = new TreeMap<>();

		implementations.put("matcher", new Callable<Boolean>() {
			Pattern ownCompiled = Compiler.compile(pattern);

			@Override
			public Boolean call() {
				return ownCompiled.matcher(text).matches();
			}
		});

		implementations.put("java", new Callable<Boolean>() {
			java.util.regex.Pattern javaCompiled = java.util.regex.Pattern
					.compile(pattern);

			@Override
			public Boolean call() {
				return javaCompiled.matcher(text).matches();
			}
		});

		System.out.println("Available Implementations:");
		for (Entry<String, Callable<Boolean>> entry : implementations
				.entrySet())
			System.out.println("  " + entry.getKey());

		System.out.print("Enter implementation: ");
		String chosenImplementation = reader.readLine();
		Callable<Boolean> task = implementations.get(chosenImplementation);

		Benchmark.Params params = new Benchmark.Params(true);
		params.setMeasureCpuTime(true);
		// params.setExecutionTimeGoal(1);
		// params.setNumberMeasurements(10);

		System.out.println(chosenImplementation + ": "
				+ new Benchmark(task, params));
		System.out.println("Pattern: " + pattern);
		System.out.println("Text: " + text);
	}
}
