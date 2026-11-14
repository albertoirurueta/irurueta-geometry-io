# Implementation plan — Improve coverage

## Task summary

Raise unit-test coverage of the library using synthetic ("fake") files that exercise all reachable scenarios, and
analyze whether crafted/malicious files can make loaders fail unexpectedly and how large a file can be read.

Choices made (confirmed with the user):
- Hardening scope: **tests + minimal fixes**. Where a crafted file makes a loader fail with anything other than
  `LoaderException`/`IOException` (OutOfMemoryError, NegativeArraySizeException, integer overflow), validate the
  declared sizes against the remaining file length and throw `LoaderException`. No new public API.
- Coverage target: **80% line coverage overall, as far as feasible** (baseline 67.5% line / 60.2% branch).
- Fixtures are generated programmatically in a JUnit `@TempDir` (no large binaries committed).

Source: GitHub issue #22
Base branch: develop
Open clarifications: none

## Acceptance criteria coverage

| # | Criterion | Covered by tasks |
|---|---|---|
| 1 | "Using fake files to test all possible scenarios" | Task 1, Task 2, Task 3, Task 4 |
| 2 | "analyze code to determine if malicious files can make the code fail" | Task 5, Task 6, Task 7, Task 8 |
| 3 | "up to what extent can large files be read" | Task 5, Task 6, Task 9 |
| 4 | derived: overall line coverage >= 80% and no regressions | Task 10 |
| 5 | derived: findings (limits and behavior) documented | Task 11 |

## Current code state

- Java 21 Maven library (`java-library`), single module, no wrapper; build with JDK 21 (default JDK here is 26 and
  fails with "invalid target release"; use `JAVA_HOME=~/Library/Java/JavaVirtualMachines/jbr-21.0.10/Contents/Home`).
  Running `mvn test` regenerates `src/main/resources/com/irurueta/geometry/io/build-info.properties` and some tests
  leave files under `src/test/java/com/irurueta/geometry/io/tmp/`; do not commit either.
- Sources in `src/main/java/com/irurueta/geometry/io/`, tests in `src/test/java/com/irurueta/geometry/io/`
  (existing resources: `*.obj`, `*.ply`, `*.stl`, `*.mtl`, images).
- Baseline per-class gaps (JaCoCo): `LoaderPLY.LoaderIteratorPLY` (485/852 lines missed), `LoaderOBJ.LoaderIteratorOBJ`
  (99/607), `LoaderBinary.LoaderIteratorBinary` (42/198), `MaterialLoaderOBJ` (42/159), `MeshWriterBinary` (36/244),
  `MeshWriterJson` (14/227), `LoaderSTL.LoaderIteratorSTL` (27/206), `LoaderPLY` (47/329), `HeaderPLY` (8/34),
  `MeshWriter.Listeners` (23/49), `MappedFileReaderAndWriter` (5/199), `Loader`/`MaterialLoader` (6 each).
  The PLY ASCII `*ReadValueFromStreamListener` classes (int8/uint8/int16/uint16/int32/uint32/float32/float64, char,
  short, int, uint, ushort, uint16...) have 0% coverage.
- Suspected robustness issues from reading the code (to be confirmed by tests first):
  - `LoaderSTL` ~line 1003: binary triangle count is an unsigned 32-bit header value, `numberOfVertices = 3 * count`,
    not validated against file length.
  - `LoaderPLY` ~lines 1465-1526: list length read from file drives `new long[listElems]`; header element instance
    counts drive loops; no caps.
  - `LoaderBinary` ~lines 525-720: `position + sizeInBytes > chunkEnd` can overflow; sizes drive `new byte[...]`.
  - `LoaderOBJ`: `reader.readLine()` is unbounded for a single huge line.
  - `MappedFileReaderAndWriter` line 59: maps the whole file (limited to 2 GB / address space).

## Implementation steps

### Group 1 (Parallelizable: yes) — coverage with fake files, one test file per task

- [x] Task 1. Cover PLY ASCII/binary value readers and iterator _(java)_ — tests added in LoaderPLYTest, LoaderIteratorPLYTest, HeaderPLYTest, new helper PLYTestFileBuilder; validated: suite green, target classes >=95% line coverage, production code unchanged
  - [x] Task 1.1. In `LoaderPLYTest` (and `LoaderIteratorPLYTest`) generate in a `@TempDir` ASCII PLY files whose
        vertex/face properties use every `DataTypePLY` (char, uchar, short, ushort, int, uint, float, double and
        their int8..float64 aliases), so each `Ascii*ReadValueFromStreamListener` is executed
  - [x] Task 1.2. Generate binary little/big-endian PLY variants with colors, normals, texture coords and
        polygon (non-triangle) faces; cover list-property branches and truncated-file branches
  - [x] Task 1.3. Cover `HeaderPLY` branches (missing `end_header`, unknown element/property, invalid format line)
- [x] Task 2. Cover OBJ and MTL loaders _(java)_ — tests added in LoaderOBJTest and MaterialLoaderOBJTest; validated: suite green, target classes >=95% line coverage, production code unchanged
  - [x] Task 2.1. In `LoaderOBJTest` generate OBJ files covering vertex/texture/normal index forms (`v`, `v/vt`,
        `v//vn`, `v/vt/vn`), negative indices, polygons/quads, groups/comments/blank lines, malformed lines
  - [x] Task 2.2. In `MaterialLoaderOBJTest` generate MTL files covering every directive (`Ka/Kd/Ks/Ns/d/Tr/illum/map_*`)
        plus malformed and missing-file cases
- [x] Task 3. Cover STL and binary loaders _(java)_ — tests added in LoaderSTLTest and LoaderBinaryTest; validated: suite green, target classes >=95% line coverage, production code unchanged
  - [x] Task 3.1. In `LoaderSTLTest` generate ASCII and binary STL files (multiple solids, empty solid, truncated)
  - [x] Task 3.2. In `LoaderBinaryTest` generate binary-format files with each chunk type (coords, colors, indices,
        texture coords, normals, bounding box) plus bad magic/unknown chunk/CRC cases
- [x] Task 4. Cover writers and remaining small gaps _(java)_ — full suite green; overall 97.5% line / 87.0% branch
  - [x] Task 4.1. Extend `MeshWriterBinaryTest`, `MeshWriterJsonTest` for uncovered branches (with/without
        colors, normals, textures; listener callbacks including `MeshWriter.Listeners`) — tests added in MeshWriterBinaryTest/MeshWriterJsonTest; validated: suite green, target classes >=95% line coverage, production code unchanged
  - [x] Task 4.2. Extend `MappedFileReaderAndWriterTest`, `FileReaderAndWriterTest` and add `LoaderTest`/
        `MaterialLoaderTest` cases for the remaining `Loader`/`MaterialLoader` branches — tests added in MappedFileReaderAndWriterTest/FileReaderAndWriterTest, new LoaderTest/MaterialLoaderTest; validated: suite green, target classes >=95% line coverage, production code unchanged
  - [x] Task 4.3. Delegate a test run of Group 1 to `iru-gate-runner` (JDK 21) and confirm it is green — full `mvn clean test` green (JDK 21)

### Group 2 (Parallelizable: yes) — malicious / large file analysis, tests first, then minimal fixes

Each task first writes a failing crafted-file test that proves the problem (or proves it is already safe), then
fixes the main code when needed. Tasks touch disjoint production and test files.

- [x] Task 5. Harden `LoaderSTL` binary header _(java)_
  - [x] Task 5.1. In `LoaderSTLTest`, craft binary STL files declaring 0, 1, `0xFFFFFFFF` triangles with a tiny body
        and assert `LoaderException` (not OOM / endless loop)
  - [x] Task 5.2. In `LoaderSTL.java` validate `numberOfTriangles * 50 + 84 <= file length` (use `long` math) before
        iterating; throw `LoaderException` otherwise
- [x] Task 6. Harden `LoaderPLY` counts and list lengths _(java)_
  - [x] Task 6.1. In `LoaderPLYTest`, craft headers with huge/negative element counts and list lengths
        (e.g. `property list uchar int` reading 255 entries from a truncated body; `element vertex 2147483647`)
  - [x] Task 6.2. In `LoaderPLY.java`/`LoaderPLY.LoaderIteratorPLY` bound element counts and `listElems` by the
        remaining bytes (binary) and surface `LoaderException` instead of allocation failures
- [x] Task 7. Harden `LoaderBinary` chunk sizes _(java)_
  - [x] Task 7.1. In `LoaderBinaryTest`, craft chunks with `coordsSizeInBytes` near `Integer.MAX_VALUE`, negative
        sizes and sizes not multiple of 4
  - [x] Task 7.2. In `LoaderBinary.java` rewrite the bound checks with overflow-safe `long` math and reject sizes
        that are negative or not a multiple of the element size with `LoaderException`
- [x] Task 8. Analyze `LoaderOBJ` / `MaterialLoaderOBJ` with hostile input _(java)_
  - [x] Task 8.1. In `LoaderOBJTest`, add tests for a very long single line (a few MB), out-of-range / zero / huge
        face indices, and binary garbage; assert only `LoaderException`/`IOException` can escape
  - [x] Task 8.2. Fix any non-`LoaderException` failure found (guard index parsing, `NumberFormatException`,
        `ArrayIndexOutOfBounds`); record the memory behavior of unbounded `readLine()` as a documented limit if left as is
- [x] Task 9. Document and test large-file behavior of readers _(java)_
  - [x] Task 9.1. In `MappedFileReaderAndWriterTest`/`FileReaderAndWriterTest`, test read at/near offsets beyond
        `Integer.MAX_VALUE` using sparse temp files where the OS allows it (skip with `Assumptions` otherwise)
  - [x] Task 9.2. In `MappedFileReaderAndWriter.java` throw a clear `IOException`/`LoaderException` when the file
        exceeds the 2 GB mapping limit instead of an obscure `IllegalArgumentException`; keep `FileReaderAndWriter`
        as the streaming alternative

### Group 3 (Parallelizable: no — needs Groups 1 and 2 finished for final numbers and findings)

- [x] Task 10. Validate coverage and quality _(java)_
  - [x] Task 10.1. Delegate to `iru-gate-runner`: full test suite on JDK 21, then JaCoCo; require >= 80% line
        coverage overall and no coverage drop in any touched class
  - [x] Task 10.2. Run Checkstyle/SpotBugs and the license-header check on new and changed files; fix findings
  - [x] Task 10.3. Ensure tests clean up `src/test/java/.../io/tmp/` (use `@TempDir`) and `build-info.properties` is not
        left modified
- [x] Task 11. Document findings
  - [x] Task 11.1. Run `/iru-update-docs`: add a "Robustness and file size limits" section to
        `docs/modules/ROOT/pages/reference.adoc` (what malformed files are rejected, which exception, memory limits per
        format) and a line in `docs/modules/ROOT/pages/whats-new.adoc`
  - [x] Task 11.2. Add an "Unreleased / Fixed" entry to `CHANGELOG.md` for the hardening changes
