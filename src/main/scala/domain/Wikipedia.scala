package domain

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source, Flow}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import net.ruippeixotog.scalascraper.dsl.DSL._

import scala.concurrent.Future

/**
 * Created by kjs8469 on 16. 10. 11..
 */
class Wikipedia(url: String) {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContextExecutor = system.dispatcher

  val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = Http().outgoingConnectionHttps(url)

  def responseFuture(uri: String): Future[HttpResponse] =
    Source.single(RequestBuilding.Get(uri))
      .via(connectionFlow)
      .runWith(Sink.head)

  def search(query: String):Future[Option[Result]] = responseFuture(query) flatMap {
    response => {
      response.status match {
        case OK => {
          Unmarshal(response.entity).to[String].map(a => Result.parse(a,query))
        }
        case d@_ => {
          Future(None)
        }
      }
    }
  }
}

object Wikipedia{
  val browser = JsoupBrowser()
  def apply(url: String) = new Wikipedia(url)
  def getContent(doc: String): String = (browser.parseString(doc) >> elementList("#mw-content-text p")).map(_ >> text("p")).mkString(" ")
}