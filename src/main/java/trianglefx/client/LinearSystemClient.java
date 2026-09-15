package trianglefx.client;

import java.util.Scanner;
import trianglefx.geometry.LinearSystem2x2;
import trianglefx.geometry.Point;

/**
 * Simple console client for solving 2x2 systems in {@code Ax = b} form.
 *
 * <p>Input format per equation row: {@code a b c}, which represents {@code ax + by = c}.</p>
 */
public final class LinearSystemClient {

	private LinearSystemClient() {
		// utility class
	}

	/**
	 * Entry point for interactive console solving.
	 *
	 * @param args command-line arguments (unused)
	 */
	public static void main(String[] args) {
		System.out.println(
			"Solve a 2x2 linear system using two equations in the form ax + by = c"
		);
		System.out.println("Enter each equation as three numbers: a b c");
		System.out.println();

		Scanner scanner = new Scanner(System.in);

		double[] eq1 = readEquationRow(scanner, 1);
		double[] eq2 = readEquationRow(scanner, 2);

		LinearSystem2x2.Result result = LinearSystem2x2.solve(
			eq1[0],
			eq1[1],
			eq1[2],
			eq2[0],
			eq2[1],
			eq2[2]
		);

		System.out.println();
		switch (result.type()) {
			case UNIQUE_SOLUTION -> {
				Point solution = result.solution();
				System.out.printf(
					"Unique solution found: x = %.6f, y = %.6f%n",
					solution.x(),
					solution.y()
				);
			}
			case NO_SOLUTION -> System.out.println(
				"No solution: lines are parallel/inconsistent."
			);
			case INFINITE_SOLUTIONS -> System.out.println(
				"Infinite solutions: equations represent the same line."
			);
		}
	}

	/**
	 * Reads one equation row from the console and validates it as three numeric values.
	 *
	 * @param scanner source scanner
	 * @param equationNumber 1-based equation index for prompt text
	 * @return parsed triple {@code [a, b, c]}
	 */
	private static double[] readEquationRow(
		Scanner scanner,
		int equationNumber
	) {
		while (true) {
			System.out.printf("Equation %d (a b c): ", equationNumber);
			String line = scanner.nextLine().trim();

			if (line.isEmpty()) {
				System.out.println(
					"Input cannot be empty. Please enter three numbers."
				);
				continue;
			}

			String[] parts = line.split("\\s+");
			if (parts.length != 3) {
				System.out.println(
					"Invalid format. Please enter exactly three numbers: a b c"
				);
				continue;
			}

			try {
				double a = Double.parseDouble(parts[0]);
				double b = Double.parseDouble(parts[1]);
				double c = Double.parseDouble(parts[2]);
				return new double[] { a, b, c };
			} catch (NumberFormatException ex) {
				System.out.println(
					"Invalid number detected. Please use numeric values only."
				);
			}
		}
	}
}
