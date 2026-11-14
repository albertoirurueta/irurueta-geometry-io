# Implementation plan — Update dependencies

## Task summary

Update outdated dependencies in `pom.xml` and fix a vulnerable transitive/direct dependency:

- `com.irurueta:irurueta-statistics` (test scope) 1.4.0 → 1.4.1
- `com.irurueta:irurueta-geometry` (compile scope) 1.6.0 → 1.6.1
- `commons-codec:commons-codec` 1.19.0 → 1.23.0 (latest on Maven Central; the issue reports CVE-2026-119078 on 1.19.0)

Choice made: commons-codec is bumped to 1.23.0, the latest release on Maven Central, since the issue does not name a specific fixed version. Only `Base64` is used (`MeshWriterJson`), a stable API.

Source: GitHub issue #21
Base branch: develop
Open clarifications: none

## Acceptance criteria coverage

| # | Criterion | Covered by tasks |
|---|---|---|
| 1 | "Update dependencies in pom.xml using latest versions" (irurueta-statistics 1.4.1, irurueta-geometry 1.6.1) | Task 1, Task 1.1 |
| 2 | "commons-codec ... has vulnerability CVE-2026-119078 and needs to be updated too" | Task 1, Task 1.2 |
| 3 | derived: project compiles and all tests pass with the new versions | Task 2 |

## Current code state

- Java 21 Maven library (`java-library`), single module, `pom.xml` at the repository root.
- `pom.xml` dependencies: junit-jupiter 5.13.4 (test), irurueta-statistics 1.4.0 (test), irurueta-geometry 1.6.0 (compile), commons-codec 1.19.0 (compile).
- commons-codec is used only in `src/main/java/com/irurueta/geometry/io/MeshWriterJson.java` (`org.apache.commons.codec.binary.Base64`).
- Docs/README mention the library's own released version (1.6.0), not these dependencies; no doc changes are required.
- No `.archive/` precedents.

## Implementation steps

### Group 1 (Parallelizable: yes)

- [x] Task 1. Bump dependency versions in `pom.xml` _(java)_
  - [x] Task 1.1. Change `irurueta-statistics` to `1.4.1` and `irurueta-geometry` to `1.6.1`
  - [x] Task 1.2. Change `commons-codec` to `1.23.0`
    - pom.xml only; no code/tests changed. Full suite 257 tests pass on JDK 21 with new versions; MeshWriterJson (sole commons-codec user) 94% line coverage; no Java sources touched so no new quality issues.

### Group 2 (Parallelizable: yes)

- [x] Task 2. Verify build and tests _(java)_ — verification only, no code changes; 257 tests pass, commons-codec 1.23.0 resolved.
  - [x] Task 2.1. Delegate to `iru-gate-runner` to run the full unit test suite (`mvn clean test`); fix any compile/test breakage caused by the new versions (e.g. `MeshWriterJson` Base64 usage)
  - [x] Task 2.2. Confirm via `mvn dependency:tree` that no vulnerable commons-codec version is resolved transitively
