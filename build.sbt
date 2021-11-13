import BuildHelper._

inThisBuild(
  List(
    organization := "dev.zio",
    homepage := Some(url("https://zio.github.io/zio-json-usage/")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "jdegoes",
        "John De Goes",
        "john@degoes.net",
        url("http://degoes.net")
      )
    ),
    pgpPassphrase := sys.env.get("PGP_PASSWORD").map(_.toArray),
    pgpPublicRing := file("/tmp/public.asc"),
    pgpSecretRing := file("/tmp/secret.asc")
  )
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fix", "; all compile:scalafix test:scalafix; all scalafmtSbt scalafmtAll")
addCommandAlias("check", "; scalafmtSbtCheck; scalafmtCheckAll; compile:scalafix --check; test:scalafix --check")

addCommandAlias(
  "testJVM",
  ";zioJsonUsageJVM/test"
)
addCommandAlias(
  "testJS",
  ";zioJsonUsageJS/test"
)
addCommandAlias(
  "testNative",
  ";zioJsonUsageNative/test:compile"
)

val zioVersion = "1.0.9"

lazy val root = project
  .in(file("."))
  .settings(
    publish / skip := true,
    unusedCompileDependenciesFilter -= moduleFilter("org.scala-js", "scalajs-library")
  )
  .aggregate(
    zioJsonUsageJVM,
    zioJsonUsageJS,
    docs
  )

lazy val zioJsonUsage = crossProject(JSPlatform, JVMPlatform)
  .in(file("zio-json-usage"))
  .settings(stdSettings("zio-json-usage"))
  .settings(crossProjectSettings)
  .settings(buildInfoSettings("zio.json.usage"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %%% "zio"          % Version.zio,
      "dev.zio" %%% "zio-json"     % Version.zioJson,
      "dev.zio" %%% "zio-test"     % Version.zio % Test,
      "dev.zio" %%% "zio-test-sbt" % Version.zio % Test
    )
  )
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))
  .enablePlugins(BuildInfoPlugin)

lazy val zioJsonUsageJS = zioJsonUsage.js
  .settings(jsSettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .settings(scalaJSUseMainModuleInitializer := true)

lazy val zioJsonUsageJVM = zioJsonUsage.jvm
  .settings(dottySettings)
  .settings(libraryDependencies += "dev.zio" %%% "zio-test-sbt" % zioVersion % Test)
  .settings(scalaReflectTestSettings)

lazy val docs = project
  .in(file("zio-json-usage-docs"))
  .settings(stdSettings("zio-json-usage"))
  .settings(
    publish / skip := true,
    moduleName := "zio-json-usage-docs",
    scalacOptions -= "-Yno-imports",
    scalacOptions -= "-Xfatal-warnings",
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(zioJsonUsageJVM),
    ScalaUnidoc / unidoc / target := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
    cleanFiles += (ScalaUnidoc / unidoc / target).value,
    docusaurusCreateSite := docusaurusCreateSite.dependsOn(Compile / unidoc).value,
    docusaurusPublishGhpages := docusaurusPublishGhpages.dependsOn(Compile / unidoc).value
  )
  .dependsOn(zioJsonUsageJVM)
  .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)

lazy val Version = new {
  val zio     = "1.0.12"
  val zioJson = "0.2.0-M2"
}
