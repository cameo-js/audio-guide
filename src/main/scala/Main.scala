import akka.actor.{Props, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.ActorMaterializer
import surpport.{Wikipedia, Google}
import util.{HtmlParserActor, RequestActor}
import surpport.UrlSurpport._
import akka.http.scaladsl.server.Directives._

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

  google ! Google("",getGooglePath("고흐"))

//  val route =
//    pathPrefix("search" / Remaining) { query =>
//      get {
//        complete(
//          HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>여기</h1>")
//        )
//      }
//    }
//
//  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
}
