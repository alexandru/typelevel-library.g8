# Typelevel Library Template

[![Build status](https://github.com/alexandru/typelevel-library.g8/workflows/build/badge.svg?branch=master)](https://github.com/alexandru/typelevel-library.g8/actions?query=branch%3Amaster+workflow%3Abuild)

This is a [Giter8][g8] template for creating libraries ready to be published.

```
sbt new alexandru/typelevel-library.g8
```

## Features

- Build setup for multiple sub-projects
- Sane Scala compiler defaults for doing FP (including [kind-projector](https://github.com/typelevel/kind-projector) and [better-monadic-for](https://github.com/oleg-py/better-monadic-for))
- Continuous integration via [GitHub Actions](https://github.com/features/actions)
- [Scala.js](https://www.scala-js.org/) cross-compilation
- [sbt-crossproject](https://github.com/portable-scala/sbt-crossproject) for managing the JVM / JS configuration
- [sbt-unidoc](https://github.com/sbt/sbt-unidoc) for unifying the API documentation of the sub-projects
- [sbt-doctest](https://github.com/tkawachi/sbt-doctest) for testing the ScalaDoc
- [sbt-microsites](https://github.com/47deg/sbt-microsites) for building the documentation website, type checked via [mdoc](https://github.com/scalameta/mdoc)
- [sbt-header](https://github.com/sbt/sbt-header) for automatic copyright headers
- [sbt-scalafmt](https://github.com/scalameta/scalafmt) default setup for auto-formatting
- Sane setup for usage of [sbt-scoverage](https://github.com/scoverage/sbt-scoverage) for code coverage
- ...

Template license
----------------

Cloned from [scala/scala-seed][source], inspired by the build definition of [Monix][monix] and by [ChristopherDavenport/library.g8][library.g8], another template with similar goals.

To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this template to the public domain worldwide.  This template is distributed without any warranty. See <http://creativecommons.org/publicdomain/zero/1.0/>.

[g8]: http://www.foundweekends.org/giter8/
[monix]: https://monix.io
[source]: https://github.com/scala/scala-seed.g8
[library.g8]: https://github.com/ChristopherDavenport/library.g8/
