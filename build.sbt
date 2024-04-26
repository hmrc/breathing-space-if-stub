import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.*

val appName = "breathing-space-if-stub"

val silencerVersion = "1.7.1"

ThisBuild / majorVersion := 1
ThisBuild / scalaVersion := "2.13.12"
ThisBuild / scalafmtOnCompile := true

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    PlayKeys.playDefaultPort := 9503,
    libraryDependencies      ++= Dependencies.compile ++ Dependencies.test,
    scalacOptions ++= Seq("-feature")
  )
  .settings(
    scoverageSettings,
    Compile / scalafmtOnCompile := true,
    Test / scalafmtOnCompile := true
  )

lazy val it = project
  .enablePlugins(play.sbt.PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(
    libraryDependencies ++= Dependencies.test,
    DefaultBuildSettings.itSettings()
  )

lazy val scoverageSettings: Seq[Setting[_]] = Seq(
  coverageExcludedPackages := List(
    "<empty>",
    ".*(Reverse|AuthService|BuildInfo|Routes).*"
  ).mkString(";"),
  coverageMinimumStmtTotal := 96,
  coverageFailOnMinimum := false,
  coverageHighlighting := true
)

