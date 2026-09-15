# Problem Statement

## Title
JavaFX Triangle Drawer Using Three Text Equations

## Objective
Build a JavaFX application where a user enters **three line equations** as text input. The application computes the pairwise intersection points of the three lines and draws the triangle formed by those intersections.

## Functional Requirements

1. The UI shall provide three text fields for line equations.
2. The user shall be able to trigger drawing with a button.
3. The system shall parse equation text into line coefficients.
4. The system shall compute intersections of each pair of lines.
5. The system shall validate that a non-degenerate triangle exists.
6. The system shall render the triangle on a JavaFX canvas.
7. The system shall display meaningful error messages for invalid input or impossible geometry.

## Non-Functional Requirements

- Simple and responsive desktop UI.
- Readable error feedback.
- Maintainable Java code with clear logic separation (parsing, geometry, drawing).

## Assumptions

- Inputs represent linear equations in `x` and `y`.
- Equations are entered in a format containing a single `=` sign.
- Numeric coefficients can be integers or decimals.

## Example Inputs

- `x + y = 8`
- `x - y = 2`
- `x = 1`

Expected behavior: application computes three vertices and draws the resulting triangle.
