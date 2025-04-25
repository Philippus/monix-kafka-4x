inThisBuild(List(
  organization := "nl.gn0s1s",
  homepage := Some(url("https://github.com/philippus/monix-kafka-4x")),
  licenses := List("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  developers := List(
    Developer(
      id="alexelcu",
      name="Alexandru Nedelcu",
      email="noreply@alexn.org",
      url=url("https://alexn.org")
    ),
    Developer(
      id="pgawrys",
      name="Piotr Gawryś",
      email="pgawrys2@gmail.com",
      url=url("https://github.com/Avasil")
    )),
))

val monixVersion = "3.4.1"

lazy val doNotPublishArtifact = Seq(
  publishArtifact := false,
  Compile / packageDoc / publishArtifact := false,
  Compile / packageSrc / publishArtifact := false,
  Compile / packageBin / publishArtifact := false
)

lazy val warnUnusedImport = Seq(
  scalacOptions ++= Seq("-Ywarn-unused:imports"),
  Compile / console / scalacOptions --= Seq("-Ywarn-unused-import", "-Ywarn-unused:imports"),
  Test / scalacOptions --= Seq("-Ywarn-unused-import", "-Ywarn-unused:imports")
)

lazy val sharedSettings = warnUnusedImport ++ Seq(
  scalacOptions ++= Seq(
    // warnings
    "-unchecked", // able additional warnings where generated code depends on assumptions
    "-deprecation", // emit warning for usages of deprecated APIs
    "-feature", // emit warning usages of features that should be imported explicitly
    // Features enabled by default
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:experimental.macros",
    // possibly deprecated options
    "-Ywarn-dead-code",
    "-language:higherKinds",
    "-language:existentials"
  ),

  // Linter
  scalacOptions ++= Seq(
    // Turns all warnings into errors ;-)
    "-Xfatal-warnings",
    // Enables linter options
    "-Xlint:adapted-args", // warn if an argument list is modified to match the receiver
    "-Xlint:nullary-unit", // warn when nullary methods return Unit
    "-Xlint:infer-any", // warn when a type argument is inferred to be `Any`
    "-Xlint:missing-interpolator", // a string literal appears to be missing an interpolator id
    "-Xlint:doc-detached", // a ScalaDoc comment appears to be detached from its element
    "-Xlint:private-shadow", // a private field (or class parameter) shadows a superclass field
    "-Xlint:type-parameter-shadow", // a local type parameter shadows a type already in scope
    "-Xlint:poly-implicit-overload", // parameterized overloaded implicit methods are not visible as view bounds
    "-Xlint:option-implicit", // Option.apply used implicit view
    "-Xlint:delayedinit-select", // Selecting member of DelayedInit
    "-Xlint:package-object-classes", // Class or object defined in package object
  ),

  doc / scalacOptions ++=
    Opts.doc.title(s"Monix"),
  doc / scalacOptions ++=
    Opts.doc.sourceUrl(s"https://github.com/monix/monix-kafka/tree/v${version.value}€{FILE_PATH}.scala"),
  doc / scalacOptions ++=
    Seq("-doc-root-content", file("docs/rootdoc.txt").getAbsolutePath),
  doc / scalacOptions ++=
    Opts.doc.version(s"${version.value}"),

  // ScalaDoc settings
  autoAPIMappings := true,
  ThisBuild / scalacOptions ++= Seq(
    // Note, this is used by the doc-source-url feature to determine the
    // relative path of a given source file. If it's not a prefix of a the
    // absolute path of the source file, the absolute path of that file
    // will be put into the FILE_SOURCE variable, which is
    // definitely not what we want.
    "-sourcepath", file(".").getAbsolutePath.replaceAll("[.]$", "")
  ),

  Test / parallelExecution := false,
  Test / testForkedParallel := false,
  Global / concurrentRestrictions += Tags.limit(Tags.Test, 1),

  headerLicense := Some(HeaderLicense.Custom(
    """|Copyright (c) 2014-2022 by The Monix Project Developers.
       |
       |Licensed under the Apache License, Version 2.0 (the "License");
       |you may not use this file except in compliance with the License.
       |You may obtain a copy of the License at
       |
       |    http://www.apache.org/licenses/LICENSE-2.0
       |
       |Unless required by applicable law or agreed to in writing, software
       |distributed under the License is distributed on an "AS IS" BASIS,
       |WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       |See the License for the specific language governing permissions and
       |limitations under the License."""
    .stripMargin)),

  // -- Settings meant for deployment on oss.sonatype.org
  sonatypeProfileName := organization.value,

  isSnapshot := version.value endsWith "SNAPSHOT",
  Test / publishArtifact := false,
  pomIncludeRepository := { _ => false }, // removes optional dependencies
)

def mimaSettings(projectName: String) = Seq(
  mimaPreviousArtifacts := Set("io.monix" %% projectName % "1.0.0-RC5")
)

lazy val commonDependencies = Seq(
  resolvers ++= Seq(
    "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases"
  ) ++ Resolver.sonatypeOssRepos("releases"),

  libraryDependencies ++= Seq(
    "io.monix" %% "monix-reactive" % monixVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
    "com.typesafe" % "config" % "1.4.3",
    "org.slf4j" % "log4j-over-slf4j" % "2.0.17",
    // For testing ...
    "ch.qos.logback" % "logback-classic" % "1.5.18" % Test,
    "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    "org.scalacheck" %% "scalacheck" % "1.18.1" % Test,
    "io.github.embeddedkafka" %% "embedded-kafka" % "4.0.1" % Test)
)

ThisBuild / scalaVersion := "2.13.16"

lazy val monixKafka = project.in(file("."))
  .settings(sharedSettings)
  .settings(doNotPublishArtifact)
  .aggregate(kafka4x)

lazy val kafka4x = project.in(file("kafka-4.0.x"))
  .settings(commonDependencies)
  .settings(mimaSettings("monix-kafka-4x"))
  .settings(
    name := "monix-kafka-4x",
    libraryDependencies += "org.apache.kafka" %  "kafka-clients" % "4.0.0" exclude("org.slf4j","slf4j-log4j12") exclude("log4j", "log4j")
  )

scalacOptions += "-Ypartial-unification"
