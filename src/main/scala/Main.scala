import akka.actor.{Props, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.ActorMaterializer
import domain.{Wikipedia, Google}
import surpport.UrlSurpport._
import akka.http.scaladsl.server.Directives._

import scala.concurrent.Future
import scala.util.Success

/**
 * Created by kjs8469 on 2016. 7. 13..
 */
object Main extends App{
  implicit val system = ActorSystem("AudioGuide")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val google = Google("www.google.co.kr")
  val wikipedia = Wikipedia("ko.wikipedia.org")

  val route =
    pathPrefix("search" / Remaining) { query =>
      get {
        complete {
          val wiki = for {
            future <- google.search(getGooglePath(query))
            result <- wikipedia.search(encodeWikiPath(Google.getTitles(future)(0)))
          } yield result
          wiki.map { body =>
            HttpEntity(ContentTypes.`application/json`, s"${body.get.toJson}")
          }
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)
}
