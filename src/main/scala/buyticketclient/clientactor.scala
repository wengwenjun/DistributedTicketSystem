package buyticketclient

import common._
import com.typesafe.config.ConfigFactory
import akka.actor.{Actor, ActorSystem, Props, ActorRef, ActorLogging}
import akka.remote.routing.RemoteRouterConfig

class ClientActor extends Actor {
	val master=context.actorSelection("akka.tcp://TicketSystem@127.0.0.1:2552/user/sys")

	def receive ={
		case ClientPurchaseTicket(choice:Int)=>
			println("Buy the ticket from kiosk "+(choice+1))
			master ! RequestKiosk(choice)
		case SuccessPurchaseTicket(act:ActorRef) =>
			println("Client: Purchase Ticket Successfully!")
			Thread.sleep(2000)
			context.system.shutdown()
		case FailPurchaseTicket(act:ActorRef) =>
			println("Client: Fail to Buy Ticket!")
			Thread.sleep(2000)
			context.system.shutdown()
	}

}
