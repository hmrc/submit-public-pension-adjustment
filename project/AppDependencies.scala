import sbt._

object AppDependencies {

  private val AllTestScope     = "test, it"
  private val bootstrapVersion = "7.22.0"
  private val hmrcMongoVersion = "1.3.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"    % bootstrapVersion,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"           % hmrcMongoVersion,
    "com.beachape"            %% "enumeratum-play-json"         % "1.6.3",
    "org.typelevel"           %% "cats-core"                    % "2.9.0",
    "uk.gov.hmrc"             %% "internal-auth-client-play-28" % "1.6.0",
    "uk.gov.hmrc.objectstore" %% "object-store-client-play-28"  % "1.1.0",
    "org.apache.xmlgraphics"   % "fop"                          % "2.8"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"         %% "bootstrap-test-play-28"  % bootstrapVersion,
    "com.vladsch.flexmark" % "flexmark-all"            % "0.64.0",
    "org.scalatest"       %% "scalatest"               % "3.2.15",
    "org.scalacheck"      %% "scalacheck"              % "1.15.4",
    "org.scalatestplus"   %% "scalacheck-1-17"         % "3.2.15.0",
    "uk.gov.hmrc.mongo"   %% "hmrc-mongo-test-play-28" % hmrcMongoVersion,
    "org.mockito"         %% "mockito-scala"           % "1.16.42",
    "org.apache.pdfbox"    % "pdfbox"                  % "2.0.27"
  ).map(_ % AllTestScope)
}
