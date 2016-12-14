import sbt._

object Version {
  val scalaTest     = "3.0.1"
  val akka          = "2.4.9"
  val scalaScraper  = "1.0.0"
  val json4s        = "3.4.0"
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
    "org.scalatest" % "scalatest_2.11" % Version.scalaTest % "test"
  )
  val scalaScraper = List(
    "net.ruippeixotog" %% "scala-scraper" % Version.scalaScraper
  )
  val json4s = List(
    "org.json4s" %% "json4s-jackson" % Version.json4s
  )
  val utilities = List(
    "ch.qos.logback" % "logback-classic" % "1.1.3"
  )
}

object Dependencies {

  import Library._

  val audioGuide = scalaScraper ::: akka ::: akkaTest ::: scalaTest ::: json4s ::: utilities
}
