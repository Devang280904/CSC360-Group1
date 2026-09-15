# TriangleFX (JavaFX)

A JavaFX desktop application that draws a triangle from **three line equations** entered as text.

The app:
- Accepts 3 equations (for example: `x + y = 8`, `x - y = 2`, `x = 1`)
- Computes pairwise intersections
- Validates that the 3 intersections form a valid triangle
- Draws the triangle on a canvas and labels vertex coordinates

## Requirements

- Java 17+
- Maven 3.8+

## Run

From project root:

```bash
mvn clean javafx:run
```

## Supported Equation Styles

Equations should represent straight lines and include `x` and/or `y` terms, such as:

- `x + y = 8`
- `2x - 3y = 10`
- `y = 2x + 1`
- `x = 4`

Spaces are optional. `*` in terms is optional (e.g., `2*x + y = 5` also works).

## Error Handling

The app reports clear errors for cases like:
- Missing or malformed equations
- Parallel or identical lines
- Intersections that do not form a valid triangle

## Project Structure

- `src/main/java/com/example/trianglefx/TriangleDrawerApp.java` — JavaFX application
- `docs/PROBLEM_STATEMENT.md` — problem statement and scope
