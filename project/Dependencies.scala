import play.core.PlayVersion.current
import sbt._

object Dependencies {

  object version {
    val jackson = "2.11.3"
  }

  val compile = Seq(
    "com.fasterxml.jackson.core"     %  "jackson-annotations"      % version.jackson,
    "com.fasterxml.jackson.core"     %  "jackson-core"             % version.jackson,
    "com.fasterxml.jackson.core"     %  "jackson-databind"         % version.jackson,
    "com.fasterxml.jackson.datatype" %  "jackson-datatype-jdk8"    % version.jackson,
    "com.fasterxml.jackson.datatype" %  "jackson-datatype-jsr310"  % version.jackson,
    "com.fasterxml.jackson.module"   %  "jackson-module-parameter-names" % version.jackson,
    "com.fasterxml.jackson.module"   %  "jackson-module-paranamer" % version.jackson,
    "com.fasterxml.jackson.module"   %% "jackson-module-scala"     % version.jackson,

    "com.google.inject"            % "guice"                % "5.0.0-BETA-1",
    "com.google.inject.extensions" % "guice-assistedinject" % "5.0.0-BETA-1",

    "uk.gov.hmrc"   %% "bootstrap-backend-play-27" % "3.0.0",
    "com.beachape"  %% "enumeratum"                % "1.6.1"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-27" % "3.0.0"  % "test, it",
    "com.typesafe.play"      %% "play-test"              % current  % "test, it",
    "org.scalatest"          %% "scalatest"              % "3.2.3"  % "test, it",
    "com.vladsch.flexmark"   %  "flexmark-all"           % "0.36.8" % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "4.0.3"  % "test, it"
  )
}
