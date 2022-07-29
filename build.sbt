import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "breathing-space-if-stub"

val silencerVersion = "1.7.1"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin)
  .settings(
    majorVersion             := 0,
    scalaVersion             := "2.12.12",
    PlayKeys.playDefaultPort := 9503,
    libraryDependencies      ++= Dependencies.compile ++ Dependencies.test,
    // ***************
    // Use the silencer plugin to suppress warnings
    scalacOptions ++= Seq("-P:silencer:pathFilters=routes", "-feature", "-Xfatal-warnings"),
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
    // ***************
  )
  .configs(IntegrationTest)
  .settings(publishingSettings: _*)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(
    scoverageSettings,
    Compile / scalafmtOnCompile := true,
    Test / scalafmtOnCompile := true
  )

scalastyleConfig := baseDirectory.value / "project" / "scalastyle-config.xml"

IntegrationTest / unmanagedResourceDirectories += baseDirectory.value / "it" / "resources"

lazy val scoverageSettings: Seq[Setting[_]] = Seq(
  coverageExcludedPackages := List(
    "<empty>",
    ".*(Reverse|AuthService|BuildInfo|Routes).*"
  ).mkString(";"),
  coverageMinimumStmtTotal := 96,
  coverageFailOnMinimum := false,
  coverageHighlighting := true,
  parallelExecution in Test := false
)

