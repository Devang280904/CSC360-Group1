# TriangleFX Design Document (Current State)

## 1) Purpose

This document describes the **current implemented state** of the repository, including architecture, modules, input format, validation flow, rendering behavior, and test coverage.

## 2) Repository Overview

The project has moved beyond docs-only and now includes:

- Maven build configuration
- Core geometry/domain modules
- Console client for `Ax = b` solving
- JavaFX UI that accepts strict matrix input (`A` and `b`), computes triangle vertices, validates, and draws the triangle on a dark themed grid canvas
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
  - strict matrix input fields for `A` (`3x2`) and `b` (`3x1`)
  - Draw/Clear controls
  - status label
  - responsive dark-themed canvas with reference grid + origin

## 5) Input Contract (Current)

The JavaFX UI now accepts **matrix format only** for the three lines:

- `A` as a `3x2` coefficient matrix
- `b` as a `3x1` constants vector

Where each row defines one line:

- `A[i,1]x + A[i,2]y = b[i]`

Example triangle-producing input:

- Row 1: `A[1,1]=1`, `A[1,2]=1`, `b[1]=8`
- Row 2: `A[2,1]=1`, `A[2,2]=-1`, `b[2]=2`
- Row 3: `A[3,1]=1`, `A[3,2]=0`, `b[3]=1`

## 6) End-to-End Draw Flow

`TriangleApp` draw flow:

1. Parse matrix entries into `A` (`3x2`) and `b` (`3x1`).
2. Convert each row into a line equation (`A[i,1]x + A[i,2]y = b[i]`).
3. Compute pairwise intersections:
   - `Row 1 & Row 2` -> `P12`
   - `Row 2 & Row 3` -> `P23`
   - `Row 3 & Row 1` -> `P31`
4. If any pair is parallel/coincident, stop with error message.
5. Validate triangle with `TriangleValidator.canFormTriangle(P12, P23, P31)`.
6. If valid, auto-fit and draw triangle on canvas.

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

- Missing required matrix/vector entries (`A[i,j]` or `b[i]`)
- Non-numeric matrix/vector values
- Invalid row definitions (e.g., both coefficients zero)
- Parallel or coincident row-pairs in pairwise intersection
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

- UI is intentionally strict matrix-only (`A` and `b`) and does not accept free-form algebraic text like `x + y = 8`.
- There is no dedicated symbolic equation parser module.
- The JavaFX app currently draws only the triangle and reference plane (no line drawing layer for the original 3 lines).

## 12) Suggested Next Steps

1. Add equation parser for forms such as `x + y = 8`, `2x - 3y = 10`, `x = 4`, `y = 2x + 1`.
2. Optionally render the original 3 lines on canvas alongside the triangle.
3. Add integration/UI tests (if introducing TestFX or equivalent).
4. Add export/snapshot option for the drawn result.
