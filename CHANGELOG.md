# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.6.0] - 2026-07-24

### Added

- Antora-based documentation site (`docs/`), including dedicated pages for each supported file format (OBJ,
  STL, PLY, binary v2, JSON) with load/write examples.
- `CHANGELOG.md`.

### Changed

- Raised the minimum/target JDK to 21.
- Migrated SonarCloud analysis from a standalone `sonar-scanner` GitHub Action to the `sonar-maven-plugin`,
  configured directly in `pom.xml`.
- Expanded `README.md` with a project status table, documentation links, and a usage example.

## [1.5.0] - 2026-03-04

### Changed

- Updated the `irurueta-geometry` dependency to 1.5.0.

## [1.4.0] - 2025-12-18

### Changed

- Updated the `irurueta-geometry` dependency to 1.4.0.

## [1.3.2] - 2025-09-22

### Changed

- Updated the `irurueta-geometry` dependency to 1.3.2.

## [1.3.1] - 2025-09-19

### Changed

- Removed the JUnit Vintage engine dependency, completing the migration to JUnit 5.
- Updated CI workflows and build plugins.

## [1.3.0] - 2024-12-07

### Changed

- Raised the minimum/target JDK to 17.
- Migrated the test suite from JUnit 4 to JUnit 5.
- Adopted Java type inference (`var`) throughout the codebase.
- Refactored `LoaderPLY`, `LoaderOBJ`, `LoaderBinary`, `LoaderSTL`, `FileReaderAndWriter` and
  `MappedFileReaderAndWriter` internals, and simplified checkstyle rules.
- Updated the `irurueta-geometry` dependency to 1.3.0 and `commons-codec` to 1.17.1.

## [1.2.0] - 2023-12-05

### Changed

- Updated the `irurueta-geometry` and `irurueta-statistics` dependencies to 1.2.0.
- General code quality improvements and javadoc wording fixes across the loader/writer classes
  (`DataTypePLY`, `LoaderOBJ`, `LoaderPLY`, `LoaderSTL`, `Illumination`, and others).
- Added a manually-triggerable `develop` GitHub Actions workflow.

## [1.1.0] - 2021-12-11

### Added

- Initial release: a library to read, write and transcode 3D mesh files.
- `Loader` implementations for OBJ, PLY (ASCII and binary) and STL (ASCII and binary) formats, plus
  chunk-based reading of 3DS files, all supporting iterative/streaming loading via `LoaderIterator` and
  listener callbacks for progress notifications.
- `MeshWriter` implementations to write meshes in custom binary and JSON formats.
- OBJ material/texture loading support (`MaterialLoaderOBJ`, `MaterialOBJ`, `Texture`).
- Low-level, endian-aware file reading/writing abstractions, including a memory-mapped file reader/writer
  (`MappedFileReaderAndWriter`) for handling large files efficiently.
- Migrated CI from Travis CI to GitHub Actions.

[Unreleased]: https://github.com/albertoirurueta/irurueta-geometry-io/compare/1.6.0...HEAD
[1.6.0]: https://github.com/albertoirurueta/irurueta-geometry-io/compare/1.5.0...1.6.0
[1.5.0]: https://github.com/albertoirurueta/irurueta-geometry-io/compare/1.4.0...1.5.0
[1.4.0]: https://github.com/albertoirurueta/irurueta-geometry-io/compare/1.3.2...1.4.0
[1.3.2]: https://github.com/albertoirurueta/irurueta-geometry-io/compare/1.3.1...1.3.2
[1.3.1]: https://github.com/albertoirurueta/irurueta-geometry-io/compare/1.3.0...1.3.1
[1.3.0]: https://github.com/albertoirurueta/irurueta-geometry-io/compare/1.2.0...1.3.0
[1.2.0]: https://github.com/albertoirurueta/irurueta-geometry-io/compare/1.1.0...1.2.0
[1.1.0]: https://github.com/albertoirurueta/irurueta-geometry-io/releases/tag/1.1.0
