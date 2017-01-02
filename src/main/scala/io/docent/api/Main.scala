package io.docent.api

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import io.docent.api.domain.{Google, Wikipedia}
import io.docent.api.surpport.UrlSurpport._
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by kjs8469 on 2016. 7. 13..
 */
object Main extends App with ActorSystemProvider{
  val log = LoggerFactory.getLogger("docent")
  val google = Google("www.google.co.kr")
  val wikipedia = Wikipedia("ko.wikipedia.org")

  val route =
    pathPrefix("search" / Remaining) { query =>
      get {
        log.info("query:{}",query)
        complete {
          val results = for {
            google <- google.search(getGooglePath(query))
          } yield {
            Google.getTitles(google).map{ title =>
              Await.result(wikipedia.search(encodeWikiPath(title)), 3.seconds).map(_.toJson).getOrElse("")
            }
          }

          results.map { body =>
            HttpEntity(ContentTypes.`application/json`, s"${body.filter(_ != "").mkString("[",",","]")}")
          }
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)
}
