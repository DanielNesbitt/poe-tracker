package fetcher

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await

/**
  * @author Daniel Nesbitt
  */
object Fetcher extends App {

  override def main(args: Array[String]) {
    val config = ConfigFactory.load("application.conf")
    val system = ActorSystem()
    system.actorOf(Props[RequestActor])
  }

}
