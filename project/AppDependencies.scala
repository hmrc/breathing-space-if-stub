import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-27" % "2.24.0",
    "uk.gov.hmrc" %% "domain"                    % "5.9.0-play-27"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-27" % "2.24.0"  % Test,
    "com.vladsch.flexmark"   %  "flexmark-all"           % "0.36.8" % "test, it",
    "com.typesafe.play"      %% "play-test"              % current   % Test,
    "org.scalatest"          %% "scalatest"              % "3.2.0"   % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"     % "4.0.3"   % "test, it"
  )
}
