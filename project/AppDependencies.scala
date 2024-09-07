import sbt._

object AppDependencies {

  private val bootstrapVersion = "9.4.0"
  private val hmrcMongoVersion = "1.9.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-30"    % bootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"           % hmrcMongoVersion,
    "com.beachape"            %% "enumeratum-play-json"         % "1.8.0",
    "org.typelevel"           %% "cats-core"                    % "2.10.0",
    "uk.gov.hmrc"             %% "internal-auth-client-play-30" % "3.0.0",
    "uk.gov.hmrc.objectstore" %% "object-store-client-play-30"  % "1.4.0",
    "org.apache.xmlgraphics"   % "fop"                          % "2.8",
    "uk.gov.hmrc"             %% "crypto-json-play-30"          % "8.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"         %% "bootstrap-test-play-30"  % bootstrapVersion % Test,
    "com.vladsch.flexmark" % "flexmark-all"            % "0.64.0"         % Test,
    "org.scalatest"       %% "scalatest"               % "3.2.15"         % Test,
    "org.scalacheck"      %% "scalacheck"              % "1.15.4"         % Test,
    "org.scalatestplus"   %% "scalacheck-1-17"         % "3.2.17.0"       % Test,
    "uk.gov.hmrc.mongo"   %% "hmrc-mongo-test-play-30" % hmrcMongoVersion % Test,
    "org.mockito"         %% "mockito-scala"           % "1.17.29"        % Test,
    "org.apache.pdfbox"    % "pdfbox"                  % "2.0.27"         % Test
  )

  val itDependencies = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % Test
  )
}
