# Implementation Plan — JavaFX Triangle Drawer

## Phase 1 — Documentation Baseline ✅ (Completed)
**Goal:** Establish project context and requirements.

- **Tasks:**
  - Finalize `README.md`
  - Finalize `docs/PROBLEM_STATEMENT.md`
- **Deliverables:**
  - `README.md`
  - `docs/PROBLEM_STATEMENT.md`
- **Exit Criteria:**
  - Team agrees documentation baseline is complete and aligned.

## Phase 2 — Project Skeleton & Build Setup ✅ (Completed)
**Goal:** Set up runnable JavaFX project structure.

- **Tasks:**
  - Initialize Java project layout (`com.trianglefx`)
  - Configure JavaFX dependencies/build tooling (`pom.xml` with Java 21, JavaFX 21, JUnit 5)
  - Add application entry point (`Main.java`, `TriangleApp.java`)
- **Deliverables:**
  - `pom.xml`
  - `src/main/java/com/trianglefx/Main.java`
- **Exit Criteria:**
  - Project builds and opens JavaFX window.

## Phase 3 — Equation Parser ✅ (Completed)
**Goal:** Parse text equations into standard line form (`Ax + By = C`).

- **Tasks:**
  - Accept forms like `x+y=8`, `2x-3y=10`, `y=2x+1`, `x=4`
  - Normalize whitespace/signs/coefficients
  - Validate malformed inputs and unsupported forms
- **Deliverables:**
  - `src/main/java/com/trianglefx/parser/EquationParser.java`
  - `src/main/java/com/trianglefx/parser/ParseException.java`
- **Exit Criteria:**
  - All valid sample equations parse correctly; invalid ones return clear errors.

## Phase 4 — Geometry Engine (Intersections + Triangle Validity) ✅ (Completed)
**Goal:** Compute vertices and determine if a valid triangle exists.

- **Tasks:**
  - Compute pairwise intersections: `P12`, `P23`, `P31` using Cramer's rule
  - Handle parallel/coincident lines
  - Reject degenerate triangles (duplicate points / collinear points / concurrent lines)
- **Deliverables:**
  - `src/main/java/com/trianglefx/model/Point.java`
  - `src/main/java/com/trianglefx/model/Line.java`
  - `src/main/java/com/trianglefx/model/Triangle.java`
  - `src/main/java/com/trianglefx/geometry/GeometryService.java`
  - `src/main/java/com/trianglefx/geometry/GeometryException.java`
- **Exit Criteria:**
  - Correct results for normal and edge-case inputs.

## Phase 5 — UI Layout & Interaction ✅ (Completed)
**Goal:** Build user interface for inputs, actions, and feedback.

- **Tasks:**
  - Add 3 equation input fields
  - Add action buttons (`Draw Triangle`, `Clear`, and 4 preset samples)
  - Add status/error message area with responsive styling
  - Add triangle properties card (vertices, sides, area, perimeter)
- **Deliverables:**
  - `src/main/java/com/trianglefx/ui/TriangleApp.java`
- **Exit Criteria:**
  - User can input equations, trigger draw flow, and see feedback.

## Phase 6 — Canvas Rendering ✅ (Completed)
**Goal:** Draw the triangle and annotate key information.

- **Tasks:**
  - Map geometric coordinates to canvas coordinates with auto-scaling and aspect-ratio preservation
  - Draw coordinate axes and background grid
  - Draw the 3 extended line equations as dashed lines
  - Draw triangle edges, semi-transparent fill, and vertex markers
  - Show vertex coordinate labels offset away from the centroid
- **Deliverables:**
  - `src/main/java/com/trianglefx/ui/TriangleCanvas.java`
- **Exit Criteria:**
  - Valid triangle is drawn clearly and consistently with auto-resizing.

## Phase 7 — Testing & Quality Assurance ✅ (Completed)
**Goal:** Validate correctness, robustness, and usability.

- **Tasks:**
  - Unit tests for parser (`EquationParserTest.java`)
  - Unit tests for geometry logic (`GeometryServiceTest.java`)
  - Verification of edge cases (parallel, concurrent, coincident, decimals, reversed variables)
- **Deliverables:**
  - `src/test/java/com/trianglefx/parser/EquationParserTest.java`
  - `src/test/java/com/trianglefx/geometry/GeometryServiceTest.java`
- **Exit Criteria:**
  - All 14 automated unit tests pass without failure.

## Phase 8 — Finalization & Submission Readiness ✅ (Completed)
**Goal:** Prepare for demo/submission.

- **Tasks:**
  - Final code cleanup/refactor
  - Update README with run/use instructions
- **Deliverables:**
  - Updated `README.md`
  - Complete, functional codebase
- **Exit Criteria:**
  - Project is demo-ready and submission-ready.
