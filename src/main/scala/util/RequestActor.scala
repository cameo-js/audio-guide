package util

import akka.actor.{ActorRef, ActorSystem, Actor}
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source, Flow}
import surpport.{Wikipedia, Web, Google}

import scala.concurrent.Future

/**
 * Created by kjs8469 on 16. 9. 11..
 */
class RequestActor(url: String, actorRef: ActorRef) extends Actor{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContextExecutor = system.dispatcher

  val connectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = Http().outgoingConnectionHttps(url)

  override def receive = {
    case google:Google => call(google)
    case wikipedia:Wikipedia => call(wikipedia)
  }

  def responseFuture(uri: String): Future[HttpResponse] =
    Source.single(RequestBuilding.Get(uri))
      .via(connectionFlow)
      .runWith(Sink.head)

  def call(web: Web) = web match {
    case Google(content, path) => {
      responseFuture(path) map {
        response => {
          response.status match {
            case OK => {
              Unmarshal(response.entity).to[String].map(body => actorRef ! Google(body,""))
            }
          }
        }
      }
    }
    case Wikipedia(content, path) => {
      responseFuture(path) map {
        response => {
          response.status match {
            case OK => {
              Unmarshal(response.entity).to[String].map(body => actorRef ! Wikipedia(body,""))
            }
          }
        }
      }
    }
  }
}
