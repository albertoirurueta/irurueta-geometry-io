# irurueta-geometry-io

Reads, writes and converts 3D files into different formats

[![Build Status](https://github.com/albertoirurueta/irurueta-geometry-io/actions/workflows/master.yml/badge.svg)](https://github.com/albertoirurueta/irurueta-geometry-io/actions)
[![Build Status](https://github.com/albertoirurueta/irurueta-geometry-io/actions/workflows/develop.yml/badge.svg)](https://github.com/albertoirurueta/irurueta-geometry-io/actions)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-geometry-io&metric=bugs)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-geometry-io)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-geometry-io&metric=code_smells)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-geometry-io)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-geometry-io&metric=coverage)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-geometry-io)

[![Duplicated lines](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-geometry-io&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-geometry-io)
[![Lines of code](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-geometry-io&metric=ncloc)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-geometry-io)

[![Maintainability](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-geometry-io&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-geometry-io)
[![Quality gate](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-geometry-io&metric=alert_status)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-geometry-io)
[![Reliability](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-geometry-io&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-geometry-io)

[![Security](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-geometry-io&metric=security_rating)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-geometry-io)
[![Technical debt](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-geometry-io&metric=sqale_index)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-geometry-io)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=albertoirurueta_irurueta-geometry-io&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-geometry-io)

## Project Status

| | |
|---|---|
| Language | Java 21 |
| Build tool | Maven |
| Current development version | 1.7.0-SNAPSHOT |
| Latest release | 1.6.0 |
| License | [Apache License 2.0](LICENSE.txt) |
| CI | GitHub Actions — builds on `develop` and on release |
| Quality | SonarCloud, JaCoCo coverage, Checkstyle, SpotBugs, PMD |

## Documentation

* [Documentation site](https://albertoirurueta.github.io/irurueta-geometry-io/) — Antora-based overview, installation and reference pages.
* [Maven Site Report](https://albertoirurueta.github.io/irurueta-geometry-io/mvn-site/) — Javadoc, Surefire, JaCoCo, Checkstyle, SpotBugs and PMD reports.
* [SonarCloud dashboard](https://sonarcloud.io/dashboard?id=albertoirurueta_irurueta-geometry-io)
* [Changelog](CHANGELOG.md)

## Installation

Add the following dependency to your project:

Latest release:
```xml
<dependency>
    <groupId>com.irurueta</groupId>
    <artifactId>irurueta-geometry-io</artifactId>
    <version>1.6.0</version>
    <scope>compile</scope>
</dependency>
```

Latest snapshot:
```xml
<dependency>
    <groupId>com.irurueta</groupId>
    <artifactId>irurueta-geometry-io</artifactId>
    <version>1.7.0-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```

## How It Works

irurueta-geometry-io reads, writes and converts 3D mesh files between different formats. It supports the OBJ, STL
and PLY formats, plus a custom binary format (binary v2) and JSON output, and is built so that a large 3D file can
be processed a small chunk at a time instead of being held entirely in memory.

Two abstractions carry the whole library:

* **`Loader`** — an abstract class with one concrete subclass per supported format (`LoaderOBJ`, `LoaderSTL`,
  `LoaderPLY`, and the binary format's `LoaderBinary`). A loader is pointed at a `File`, and its `load()` method
  returns a `LoaderIterator` that yields the mesh's vertices, textures and other chunks a piece at a time, so
  files far larger than available memory can still be processed.
* **`MeshWriter`** — an abstract class that pairs a `Loader` with an `OutputStream` and re-encodes whatever the
  loader reads into a different format (`MeshWriterBinary` for the library's own binary format, `MeshWriterJson`
  for JSON).

For example, to convert an OBJ file into the library's binary format:

```java
final var loader = new LoaderOBJ(new File("input.obj"));
try (final var outStream = new FileOutputStream("output.bin")) {
    final var writer = new MeshWriterBinary(loader, outStream);
    writer.write();
}
```

See the [documentation site](https://albertoirurueta.github.io/irurueta-geometry-io/) for the full list of
supported formats and further usage details.

## License

This project is licensed under the [Apache License 2.0](LICENSE.txt).
