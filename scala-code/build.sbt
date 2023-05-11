val scala3Version = "3.2.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Scala Code",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test,
    libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.17.0" % "test",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.9.0",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.4.10",
    testFrameworks += new TestFramework("munit.Framework")
  )
