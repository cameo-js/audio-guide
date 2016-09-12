import sbt._

object Version {
  val scalaTest     = "2.2.4"
  val akka          = "2.4.9"
  val scalaScraper  = "1.0.0"
}

object Library {
  val akka = List(
    "com.typesafe.akka" %% "akka-actor" % Version.akka,
    "com.typesafe.akka" %% "akka-http-core" % Version.akka,
    "com.typesafe.akka" %% "akka-slf4j" % Version.akka,
    "com.typesafe.akka" %% "akka-remote" % Version.akka,
    "com.typesafe.akka" %% "akka-http-experimental" % Version.akka
  )
  val akkaTest = List(
    "com.typesafe.akka" %% "akka-testkit" % Version.akka
  )
  val scalaTest = List(
    "org.scalatest" %% "scalatest" % Version.scalaTest
  )
  val scalaScraper = "net.ruippeixotog" %% "scala-scraper" % Version.scalaScraper
}

object Dependencies {

  import Library._

  val audioGuide = scalaScraper :: akka ::: akkaTest ::: scalaTest
}
