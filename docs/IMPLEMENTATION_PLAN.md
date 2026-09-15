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

## Phase 2 — Project Skeleton & Build Setup
**Goal:** Set up runnable JavaFX project structure.

- **Tasks:**
  - Initialize Java project layout
  - Configure JavaFX dependencies/build tooling
  - Add application entry point
- **Deliverables:**
  - Build configuration
  - Base source structure
- **Exit Criteria:**
  - Project builds and opens a blank JavaFX window.

## Phase 3 — Equation Parser
**Goal:** Parse text equations into standard line form (`Ax + By = C`).

- **Tasks:**
  - Accept forms like `x+y=8`, `2x-3y=10`, `y=2x+1`, `x=4`
  - Normalize whitespace/signs/coefficients
  - Validate malformed inputs and unsupported forms
- **Deliverables:**
  - Parser module and input validation rules
- **Exit Criteria:**
  - All valid sample equations parse correctly; invalid ones return clear errors.

## Phase 4 — Geometry Engine (Intersections + Triangle Validity)
**Goal:** Compute vertices and determine if a valid triangle exists.

- **Tasks:**
  - Compute pairwise intersections: `P12`, `P23`, `P31`
  - Handle parallel/coincident lines
  - Reject degenerate triangles (duplicate points / collinear points)
- **Deliverables:**
  - Geometry utility module
- **Exit Criteria:**
  - Correct results for normal and edge-case inputs.

## Phase 5 — UI Layout & Interaction
**Goal:** Build user interface for inputs, actions, and feedback.

- **Tasks:**
  - Add 3 equation input fields
  - Add action buttons (`Draw`, `Clear`)
  - Add status/error message area
- **Deliverables:**
  - Functional JavaFX UI with wired handlers
- **Exit Criteria:**
  - User can input equations, trigger draw flow, and see feedback.

## Phase 6 — Canvas Rendering
**Goal:** Draw the triangle and annotate key information.

- **Tasks:**
  - Map geometric coordinates to canvas coordinates
  - Draw triangle edges/fill and vertex markers
  - Optionally show vertex coordinate labels
- **Deliverables:**
  - Rendering module integrated with geometry output
- **Exit Criteria:**
  - Valid triangle is drawn clearly and consistently.

## Phase 7 — Testing & Quality Assurance
**Goal:** Validate correctness, robustness, and usability.

- **Tasks:**
  - Unit tests for parser and geometry logic
  - Manual UI tests for valid/invalid scenarios
  - Fix defects from testing
- **Deliverables:**
  - Test cases and bug-fix updates
- **Exit Criteria:**
  - Core functionality passes tests with no critical defects.

## Phase 8 — Finalization & Submission Readiness
**Goal:** Prepare for demo/submission.

- **Tasks:**
  - Final code cleanup/refactor
  - Update README with run/use instructions
  - Add screenshots/demo notes (optional)
- **Deliverables:**
  - Final codebase and polished documentation
- **Exit Criteria:**
  - Project is demo-ready and submission-ready.
