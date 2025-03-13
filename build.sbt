import uk.gov.hmrc.DefaultBuildSettings.itSettings

val scala3_3_4 = "3.3.4"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := scala3_3_4

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(itSettings())
  .settings(libraryDependencies ++= AppDependencies.itDependencies)

lazy val microservice = Project("submit-public-pension-adjustment", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin, ScalafmtPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    scalaVersion := "3.3.4",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    dependencyOverrides += "commons-codec" % "commons-codec" % "1.12",
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions ++= Seq(
      "-Wconf:src=routes/.*:s",
      "-Wconf:msg=Flag.*repeatedly:s"
    ),
    PlayKeys.playDefaultPort := 12803
  )
  .settings(inConfig(Test)(testSettings): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)
  .settings(scoverageSettings)

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork := true,
  unmanagedSourceDirectories += baseDirectory.value / "test-utils"
)

lazy val scoverageSettings =
  Seq(
    coverageExcludedPackages := """;uk\.gov\.hmrc\.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*\.Reverse[^.]*;testonly""",
    coverageExcludedFiles := "<empty>;.*javascript.*;.*Routes.*;.*testonly.*",
    coverageFailOnMinimum := true,
    coverageHighlighting := true,
    coverageMinimumStmtTotal := 80
  )
