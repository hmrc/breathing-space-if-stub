import sbt._

object Dependencies {

  val bootstrapVersion = "9.7.0"
  val playVersion = "play-30"

  val compile = Seq(
    "uk.gov.hmrc"                  %% s"bootstrap-backend-$playVersion"  % bootstrapVersion
  )

  val test = Seq(
    "uk.gov.hmrc"            %% s"bootstrap-test-$playVersion" % bootstrapVersion
  ).map(_ % "test")
}
