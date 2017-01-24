package buyticketclient

import common._
import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorRef, ActorLogging, ActorSystem, Props}
import akka.remote.routing.RemoteRouterConfig

object Client extends App {
	val system = ActorSystem("TicketClient", ConfigFactory.load.getConfig("client"))
	val client = system.actorOf(Props[ClientActor], name="client")
	var numberofKiosks=ConfigFactory.load.getInt("numberofKiosks")
	println("Client is Ready:")
	println(client.path)

	def printFormat()={
		println("Welcome to Wenjun's Ticket System, there are "+numberofKiosks+" kiosks in the system:")
		println("Choose from: 1 to "+numberofKiosks)
	}
	printFormat()
	var choice=readLine("\nPlease input your choice: ")
	client ! ClientPurchaseTicket(choice.toInt-1)
}
