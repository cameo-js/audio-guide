package io.docent.api

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

/**
 * Created by kjs8469 on 16. 12. 14..
 */
trait ActorSystemProvider {
  implicit val system = ActorSystem("Docent")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
}
