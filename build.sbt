val githubRepo = "scalajs-ol3-cesium"
val ol3cesiumVersion = "1.10"

val commonSettings = Seq(
  organization := "com.github.maprohu",
  version := "0.1.0-SNAPSHOT",
  resolvers += Resolver.sonatypeRepo("snapshots"),

  scalaVersion := "2.11.7",
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomIncludeRepository := { _ => false },
  licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php")),
  homepage := Some(url(s"https://github.com/maprohu/${githubRepo}")),
  pomExtra := (
      <scm>
        <url>git@github.com:maprohu/{githubRepo}.git</url>
        <connection>scm:git:git@github.com:maprohu/{githubRepo}.git</connection>
      </scm>
      <developers>
        <developer>
          <id>maprohu</id>
          <name>maprohu</name>
          <url>https://github.com/maprohu</url>
        </developer>
      </developers>
    )
)

val noPublish = Seq(
  publishArtifact := false,
  publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))
)

//lazy val jsdocgenLib = ProjectRef(uri("../scalajs-jsdocgen"), "lib")

lazy val facade = project
  .settings(commonSettings)
  .enablePlugins(JsdocPlugin, ScalaJSPlugin)
//  .dependsOn(jsdocgenLib)
  .settings(
    publishArtifact in (Compile, packageDoc) := false,
    name := "scalajs-ol3-cesium",
    jsdocRunSource := Some(
      uri(s"https://github.com/openlayers/ol3-cesium.git#v${ol3cesiumVersion}")
    ),
  jsdocTarget := (sourceManaged in Compile).value,
    jsdocRunInputs := Seq("src", "externs"),
    jsdocRunTarget := target.value / "ol3-cesium-jsdoc.json",


  // comment to do jsdoc run
      jsdocDocletsFile := target.value / "ol3-cesium-jsdoc.json",
//    jsdocDocletsFile := (sourceDirectory in Compile).value / "jsdoc" / s"ol3-cesium-${ol3cesiumVersion}-jsdoc.json",

    jsdocGlobalScope := Seq("ol3cesium"),
    jsdocUtilScope := "pkg",
    sourceGenerators in Compile += jsdocGenerate.taskValue,
//    jsDependencies ++= Seq(
//      "org.webjars" % "openlayers" % ol3cesiumVersion / s"webjars/openlayers/${ol3cesiumVersion}/ol-debug.js" minified s"webjars/openlayers/${ol3cesiumVersion}/ol.js"
//    ),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.0"
    ),
    mappings in (Compile, packageSrc) ++=
      (managedSources in Compile).value pair relativeTo((sourceManaged in Compile).value)

  )

lazy val testapp = project
  .settings(commonSettings)
  .settings(noPublish)
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(facade)
  .settings(
    persistLauncher in Compile := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.0"
    )

  )

lazy val root = (project in file("."))
  .settings(noPublish)
  .aggregate(facade, testapp)