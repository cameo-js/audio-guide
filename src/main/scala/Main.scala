import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.ActorMaterializer
import surpport.{Google, Wikipedia}
import util.{HtmlParserActor, RequestActor}
import surpport.UrlSurpport._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout

import scala.util.Success

/**
 * Created by kjs8469 on 2016. 7. 13..
 */
object Main extends App{
  implicit val system = ActorSystem("AudioGuide")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val wikipediaParser = system.actorOf(Props(new HtmlParserActor(null)), "wikipediaParserActor")
  val wikipedia = system.actorOf(Props(new RequestActor("ko.wikipedia.org", wikipediaParser)), "wikipediaActor")

  val googleParser = system.actorOf(Props(new HtmlParserActor(wikipedia)), "googleParserActor")
  val google = system.actorOf(Props(new RequestActor("www.google.co.kr", googleParser)), "googleActor")

  val testactor1 = system.actorOf(Props(classOf[TestActor], 1)  )
  val testactor2 = system.actorOf(Props(classOf[TestActor], 2)  )


//  google ! Google("",getGooglePath("고흐"))

  import akka.util.Timeout

  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  val route =
    pathPrefix("search" / Remaining) { query =>
      get {
        println(s"진입 => ${query}")

        val f1 =akka.pattern.ask(testactor1, query).mapTo[String]
        val f2 =akka.pattern.ask(testactor2, query).mapTo[String]
        def result = for {
          a<-f1; b<-f2
        } yield a + b
        result

        onComplete( result ) {
          case Success(str) => {
            complete(str)
          }
        }
      }
    }
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
}
