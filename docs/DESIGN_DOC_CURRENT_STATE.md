# TriangleFX Design Document (Current State)

## 1) Purpose

This document describes the **current implemented state** of the repository, including architecture, modules, input format, validation flow, rendering behavior, and test coverage.

## 2) Repository Overview

The project has moved beyond docs-only and now includes:

- Maven build configuration
- Core geometry/domain modules
- Console client for `Ax = b` solving
- JavaFX UI that accepts three equations, computes triangle vertices, validates, and draws the triangle on a dark themed grid canvas
- JUnit test suite for geometry and linear-system logic

## 3) Technology Stack

- **Language:** Java 17
- **Build:** Maven
- **UI:** JavaFX (`javafx-controls`)
- **Testing:** JUnit 5

## 4) Package Structure

### `trianglefx.geometry`
Core math and geometry utilities:

- `Point` — immutable 2D point record (`x`, `y`)
- `LineEquation` — line model in standard form `Ax + By = C` with validation (`A` and `B` cannot both be zero)
- `LineIntersection` — computes pairwise line relationship and intersection point if unique
  - `INTERSECTING`
  - `PARALLEL`
  - `COINCIDENT`
- `LinearSystem2x2` — solves `Ax = b` for 2x2 systems with classification:
  - `UNIQUE_SOLUTION`
  - `NO_SOLUTION`
  - `INFINITE_SOLUTIONS`
- `TriangleValidator` — checks if 3 points form a non-degenerate triangle

### `trianglefx.client`
- `LinearSystemClient` — console client that reads two equations in `a b c` format and solves them.

### `trianglefx.app`
- `TriangleApp` — JavaFX application with:
  - 3 equation inputs (`a b c` each)
  - Draw/Clear controls
  - status label
  - responsive dark-themed canvas with reference grid + origin

## 5) Input Contract (Current)

The implemented input format is:

- `a b c` meaning `ax + by = c`

Example triangle-producing set:

- `1 1 8`
- `1 -1 2`
- `1 0 1`

## 6) End-to-End Draw Flow

`TriangleApp` draw flow:

1. Parse three input rows into `LineEquation`.
2. Compute pairwise intersections:
   - `L1 & L2` -> `P12`
   - `L2 & L3` -> `P23`
   - `L3 & L1` -> `P31`
3. If any pair is parallel/coincident, stop with error message.
4. Validate triangle with `TriangleValidator.canFormTriangle(P12, P23, P31)`.
5. If valid, auto-fit and draw triangle on canvas.

## 7) Rendering Design

Current canvas behavior:

- Dark theme background
- Square reference grid (adaptive spacing)
- X-axis and Y-axis
- Origin marker `O(0, 0)`
- Triangle fill + stroke + labeled vertices
- Canvas resizes with window; redraw occurs on resize

### Interaction Model

- **Pan/zoom has been removed.**
- View is auto-fit to the current triangle when drawing.
- Clear resets to default reference viewport around origin.

## 8) Error Handling Strategy

Errors are surfaced as friendly status messages in UI:

- Empty input
- Wrong token count (not exactly 3 values)
- Non-numeric values
- Parallel or coincident lines in pairwise intersection
- Degenerate triangle (collinear/duplicate points)

## 9) Testing Status

Current automated tests include:

- `LineEquationTest`
- `LineIntersectionTest`
- `LinearSystem2x2Test`
- `TriangleValidatorTest`

Coverage focuses on:

- Valid/invalid construction
- Unique/parallel/coincident or infinite/no-solution cases
- Null/shape validation paths
- Degenerate and valid triangle detection

## 10) Build & Run

From repository root:

- Run tests: `mvn test`
- Run JavaFX app: `mvn javafx:run`

## 11) Constraints / Known Gaps

- UI currently accepts only numeric `a b c` format, not free-form algebraic text like `x + y = 8`.
- There is no dedicated parser module yet for equation strings in symbolic format.
- The JavaFX app currently draws only the triangle and reference plane (no line drawing layer for the original 3 lines).

## 12) Suggested Next Steps

1. Add equation parser for forms such as `x + y = 8`, `2x - 3y = 10`, `x = 4`, `y = 2x + 1`.
2. Optionally render the original 3 lines on canvas alongside the triangle.
3. Add integration/UI tests (if introducing TestFX or equivalent).
4. Add export/snapshot option for the drawn result.
