import sbt._

object Dependencies {

  val bootstrapVersion = "8.5.0"
  val playVersion = "play-30"

  val compile = Seq(
    "uk.gov.hmrc"                  %% s"bootstrap-backend-$playVersion"  % bootstrapVersion
  )

  val test = Seq(
    "uk.gov.hmrc"            %% s"bootstrap-test-$playVersion" % bootstrapVersion,
    "com.vladsch.flexmark"   %  "flexmark-all"           % "0.62.2",
    "org.scalatest"          %% "scalatest"              % "3.2.12",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0"
  ).map(_ % "test")
}
