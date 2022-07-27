import play.core.PlayVersion.current
import sbt._

object Dependencies {

  val compile = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-28"  % "6.4.0",
    "com.beachape"                 %% "enumeratum"                 % "1.7.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"       % "2.13.3"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % "6.4.0"  % "it",
    "com.vladsch.flexmark"   %  "flexmark-all"           % "0.62.2" % "it",
    "com.typesafe.play"      %% "play-test"              % current  % "it",
    "org.scalatest"          %% "scalatest"              % "3.2.12"  % "it",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0"  % "it"
  )
}
