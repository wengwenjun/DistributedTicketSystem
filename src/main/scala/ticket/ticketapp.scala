package ticket

import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Props}
import common._

object TicketApplication extends App{
  val system= ActorSystem("TicketSystem",ConfigFactory.load.getConfig("server"))
  val master = system.actorOf(Props[TicketMaster],name="sys")
}
