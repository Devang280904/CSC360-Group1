package trianglefx.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import trianglefx.geometry.LineEquation;
import trianglefx.geometry.LineIntersection;
import trianglefx.geometry.Point;
import trianglefx.geometry.TriangleValidator;

/**
 * JavaFX client that accepts three equations in "a b c" format (ax + by = c),
 * computes pairwise intersections, validates triangle existence, and draws it.
 */
public final class TriangleApp extends Application {

	private static final double CANVAS_WIDTH = 760;
	private static final double CANVAS_HEIGHT = 520;
	private static final double MARGIN = 40;

	private TextField eq1Field;
	private TextField eq2Field;
	private TextField eq3Field;
	private Label statusLabel;
	private Canvas canvas;

	@Override
	public void start(Stage stage) {
		eq1Field = new TextField("1 1 8");
		eq2Field = new TextField("1 -1 2");
		eq3Field = new TextField("1 0 1");

		statusLabel = new Label(
			"Enter 3 equations as: a b c (means ax + by = c)"
		);
		canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
		clearCanvas();

		Button drawButton = new Button("Draw");
		drawButton.setOnAction(event -> onDraw());

		Button clearButton = new Button("Clear");
		clearButton.setOnAction(event -> onClear());

		GridPane inputs = new GridPane();
		inputs.setHgap(10);
		inputs.setVgap(8);
		inputs.addRow(0, new Label("Equation 1 (a b c):"), eq1Field);
		inputs.addRow(1, new Label("Equation 2 (a b c):"), eq2Field);
		inputs.addRow(2, new Label("Equation 3 (a b c):"), eq3Field);

		HBox controls = new HBox(10, drawButton, clearButton);

		VBox top = new VBox(10, inputs, controls, statusLabel);
		top.setPadding(new Insets(12));

		BorderPane root = new BorderPane();
		root.setTop(top);
		root.setCenter(canvas);
		BorderPane.setMargin(canvas, new Insets(0, 12, 12, 12));

		stage.setTitle("TriangleFX");
		stage.setScene(new Scene(root, 800, 700));
		stage.show();
	}

	private void onDraw() {
		try {
			LineEquation l1 = parseLine(eq1Field.getText(), "Equation 1");
			LineEquation l2 = parseLine(eq2Field.getText(), "Equation 2");
			LineEquation l3 = parseLine(eq3Field.getText(), "Equation 3");

			Point p12 = uniqueIntersectionOrThrow(
				l1,
				l2,
				"Equation 1 & Equation 2"
			);
			Point p23 = uniqueIntersectionOrThrow(
				l2,
				l3,
				"Equation 2 & Equation 3"
			);
			Point p31 = uniqueIntersectionOrThrow(
				l3,
				l1,
				"Equation 3 & Equation 1"
			);

			if (!TriangleValidator.canFormTriangle(p12, p23, p31)) {
				clearCanvas();
				statusLabel.setText(
					"No valid triangle can be formed (degenerate/collinear points)."
				);
				return;
			}

			drawTriangle(p12, p23, p31);
			statusLabel.setText(
				String.format(
					"Triangle drawn. Vertices: P12(%.3f, %.3f), P23(%.3f, %.3f), P31(%.3f, %.3f)",
					p12.x(),
					p12.y(),
					p23.x(),
					p23.y(),
					p31.x(),
					p31.y()
				)
			);
		} catch (IllegalArgumentException ex) {
			clearCanvas();
			statusLabel.setText("Input error: " + ex.getMessage());
		}
	}

	private void onClear() {
		eq1Field.clear();
		eq2Field.clear();
		eq3Field.clear();
		clearCanvas();
		statusLabel.setText("Cleared. Enter equations as: a b c");
	}

	private LineEquation parseLine(String raw, String label) {
		if (raw == null || raw.trim().isEmpty()) {
			throw new IllegalArgumentException(
				label + " is empty. Expected: a b c"
			);
		}

		String[] parts = raw.trim().split("\\s+");
		if (parts.length != 3) {
			throw new IllegalArgumentException(
				label + " must contain exactly 3 numbers: a b c"
			);
		}

		try {
			double a = Double.parseDouble(parts[0]);
			double b = Double.parseDouble(parts[1]);
			double c = Double.parseDouble(parts[2]);
			return new LineEquation(a, b, c);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException(
				label + " contains invalid number(s).",
				ex
			);
		}
	}

	private Point uniqueIntersectionOrThrow(
		LineEquation l1,
		LineEquation l2,
		String label
	) {
		LineIntersection.Result result = LineIntersection.find(l1, l2);

		return switch (result.type()) {
			case INTERSECTING -> result.point();
			case PARALLEL -> throw new IllegalArgumentException(
				label + " are parallel (no intersection)."
			);
			case COINCIDENT -> throw new IllegalArgumentException(
				label + " are coincident (infinite intersections)."
			);
		};
	}

	private void clearCanvas() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	private void drawTriangle(Point p1, Point p2, Point p3) {
		clearCanvas();

		ScreenPoint s1;
		ScreenPoint s2;
		ScreenPoint s3;

		Transform transform = buildTransform(p1, p2, p3);
		s1 = toScreen(p1, transform);
		s2 = toScreen(p2, transform);
		s3 = toScreen(p3, transform);

		GraphicsContext gc = canvas.getGraphicsContext2D();

		double[] xs = { s1.x, s2.x, s3.x };
		double[] ys = { s1.y, s2.y, s3.y };

		gc.setFill(Color.rgb(66, 135, 245, 0.28));
		gc.fillPolygon(xs, ys, 3);

		gc.setStroke(Color.DODGERBLUE);
		gc.setLineWidth(2.0);
		gc.strokePolygon(xs, ys, 3);

		drawVertex(gc, s1, "P12", p1);
		drawVertex(gc, s2, "P23", p2);
		drawVertex(gc, s3, "P31", p3);
	}

	private void drawVertex(
		GraphicsContext gc,
		ScreenPoint sp,
		String label,
		Point worldPoint
	) {
		double r = 4;
		gc.setFill(Color.CRIMSON);
		gc.fillOval(sp.x - r, sp.y - r, 2 * r, 2 * r);

		gc.setFill(Color.BLACK);
		gc.fillText(
			String.format(
				"%s (%.2f, %.2f)",
				label,
				worldPoint.x(),
				worldPoint.y()
			),
			sp.x + 6,
			sp.y - 6
		);
	}

	private Transform buildTransform(Point p1, Point p2, Point p3) {
		double minX = Math.min(p1.x(), Math.min(p2.x(), p3.x()));
		double maxX = Math.max(p1.x(), Math.max(p2.x(), p3.x()));
		double minY = Math.min(p1.y(), Math.min(p2.y(), p3.y()));
		double maxY = Math.max(p1.y(), Math.max(p2.y(), p3.y()));

		double worldW = maxX - minX;
		double worldH = maxY - minY;

		if (worldW < 1e-9) {
			minX -= 1;
			maxX += 1;
			worldW = maxX - minX;
		}
		if (worldH < 1e-9) {
			minY -= 1;
			maxY += 1;
			worldH = maxY - minY;
		}

		double sx = (canvas.getWidth() - 2 * MARGIN) / worldW;
		double sy = (canvas.getHeight() - 2 * MARGIN) / worldH;
		double scale = Math.min(sx, sy);

		return new Transform(minX, minY, scale);
	}

	private ScreenPoint toScreen(Point world, Transform t) {
		double x = MARGIN + (world.x() - t.minX) * t.scale;
		double y = canvas.getHeight() - MARGIN - (world.y() - t.minY) * t.scale;
		return new ScreenPoint(x, y);
	}

	private static final class Transform {

		private final double minX;
		private final double minY;
		private final double scale;

		private Transform(double minX, double minY, double scale) {
			this.minX = minX;
			this.minY = minY;
			this.scale = scale;
		}
	}

	private static final class ScreenPoint {

		private final double x;
		private final double y;

		private ScreenPoint(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
