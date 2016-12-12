package domain

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source, Flow}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import net.ruippeixotog.scalascraper.dsl.DSL._

import scala.concurrent.Future

/**
 * Created by kjs8469 on 16. 10. 11..
 */
class Google(url: String) {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContextExecutor = system.dispatcher

  val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = Http().outgoingConnectionHttps(url)

  def responseFuture(uri: String): Future[HttpResponse] =
    Source.single(RequestBuilding.Get(uri))
      .via(connectionFlow)
      .runWith(Sink.head)

  def search(query: String):Future[String] = responseFuture(query) flatMap {
    response => {
      response.status match {
        case OK => {
          Unmarshal(response.entity).to[String]
        }
        case d@_ => {
          Future("")
        }
      }
    }
  }
}
object Google {
  val browser = JsoupBrowser()
  def apply(url: String) = new Google(url)
  def getTitle(doc: String): String = browser.parseString(doc) >> text("title")
  def getTitles(doc: String): List[String] = (browser.parseString(doc) >> elementList(".s")).map(parseTitleFromElement)
  def parseTitleFromElement(elem: Element): String = elem >> text("cite")
}