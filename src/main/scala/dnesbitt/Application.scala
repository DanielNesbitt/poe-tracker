package dnesbitt

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import fetcher.RequestActor

/**
  * @author Daniel Nesbitt
  */
object Application extends App {

  override def main(args: Array[String]) {
    val config = ConfigFactory.load("application.conf")
    val system = ActorSystem()
    system.actorOf(Props(classOf[RequestActor], ""))
  }

}
