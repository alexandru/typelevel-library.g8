# Typelevel Library Template

[![Build status](https://github.com/alexandru/typelevel-library.g8/workflows/build/badge.svg?branch=master)](https://github.com/alexandru/typelevel-library.g8/actions?query=branch%3Amaster+workflow%3Abuild)

This is a [Giter8][g8] template for creating libraries ready to be published.

## Usage

```
sbt new alexandru/typelevel-library.g8
```

## Features

- Build setup for multiple sub-projects
- Sane Scala compiler defaults for doing FP (including [kind-projector](https://github.com/typelevel/kind-projector) and [better-monadic-for](https://github.com/oleg-py/better-monadic-for))
- Continuous integration via [GitHub Actions](https://github.com/features/actions)
- Usual [contributing](./src/main/g8/CONTRIBUTING.md), [code of conduct](./src/main/g8/CODE_OF_CONDUCT.md), [license](./src/main/g8/LICENSE.md) boilerplate
- [Scala.js](https://www.scala-js.org/) cross-compilation
- [sbt-crossproject](https://github.com/portable-scala/sbt-crossproject) for managing the JVM / JS configuration
- [sbt-unidoc](https://github.com/sbt/sbt-unidoc) for unifying the API documentation of the sub-projects
- [sbt-doctest](https://github.com/tkawachi/sbt-doctest) for testing the ScalaDoc
- [sbt-microsites](https://github.com/47deg/sbt-microsites) for building the documentation website, type checked via [mdoc](https://github.com/scalameta/mdoc)
- [sbt-header](https://github.com/sbt/sbt-header) for automatic copyright headers
- [sbt-scalafmt](https://github.com/scalameta/scalafmt) default setup for auto-formatting
- [sbt-tpolecat](https://github.com/DavidGregory084/sbt-tpolecat) for sane Scalac compiler options with most linter options on
- [sbt-ci-release](https://github.com/olafurpg/sbt-ci-release) for managing the versioning and the release to Sonatype
  - [sbt-git](https://github.com/sbt/sbt-git) setup for version management based on Git tags and SHAs
  - [sbt-dynver](https://github.com/dwijnand/sbt-dynver) a ready-made setup of `sbt-git` options for dynamic version management
  - [sbt-sonatype](https://github.com/xerial/sbt-sonatype) for faster and easier releases
- [sbt-scoverage](https://github.com/scoverage/sbt-scoverage) for code coverage with sane setup
- ...

## Configuration of Automatic Releases to Sonatype

The created project already has workflows defined for building and releasing the library on Sonatype via [GitHub Actions](https://github.com/features/actions). For automated releases to work, you need to configure:

- `GITHUB_TOKEN` — for automatically publishing the documentation microsite with the `repo` scope:
  - see [GitHub's documentation](https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line)
  - [Quick link (click here)](https://github.com/settings/tokens/new?scopes=repo&description=sbt-microsites)
- ...

To publish the website to [GitHub Pages](https://pages.github.com/), it is recommended that you first create the `gh-pages` branch:

```sh
git checkout --orphan gh-pages
git rm --cached -r .
touch index.html && git add index.html
git commit -am 'Initial commit'
git push --set-upstream origin gh-pages
git add .
git reset --hard HEAD
git checkout master
```

Template license
----------------

Cloned from [scala/scala-seed][source], inspired by the build definition of [Monix][monix] and by [ChristopherDavenport/library.g8][library.g8], another template with similar goals.

To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this template to the public domain worldwide.  This template is distributed without any warranty. See <http://creativecommons.org/publicdomain/zero/1.0/>.

[g8]: http://www.foundweekends.org/giter8/
[monix]: https://monix.io
[source]: https://github.com/scala/scala-seed.g8
[library.g8]: https://github.com/ChristopherDavenport/library.g8/
