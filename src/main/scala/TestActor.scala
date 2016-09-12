import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}

/**
  * Created by bistros on 2016. 9. 12..
  */
class TestActor(int:Int) extends Actor with ActorLogging {
  override def receive: Receive = {
    case s:String => {
      Thread.sleep(int * 1000 )
      sender() ! "suceesss!!!!asdasd"
    }
    case d@_ => {
      sender() ! "x"
    }

  }
}
