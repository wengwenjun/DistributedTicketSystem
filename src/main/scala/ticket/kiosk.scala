package ticket

import akka.actor.{Actor,ActorRef,Props}
import scala.collection.mutable.Map
import com.typesafe.config.ConfigFactory
import common._

class Kiosk extends Actor{
	var actorList:Map[String,ActorRef] = Map()
	var ticketNum=0		//the remaining ticket number from the kiosk
	var requestForMoreTicket=false	//to remember if the kiosk has been asked have ticket or not
	var remainTicketInTheLoop=true	//the flag to mark if there are some ticket in the loop
	var ticketsInOther=true		//to remember there are some other tickets in other kiosk
	var chunkSize=ConfigFactory.load.getInt("chunkSize")
	var waitingBuyer:List[ActorRef]=List[ActorRef]()
	def receive ={
		case Init(next:ActorRef) =>
			actorList+=("master"->sender)
			sender ! ACK
		case MakeRing(next:ActorRef)=>
			actorList+=("next"->next)
			sender ! RACK
			print("Kiosk: "+self+" of next "+next+"\n")
		case SendTicket(count:Int) =>
			ticketNum+=chunkSize
			print("Kiosk: Init Get "+chunkSize+" tickets.\n")
			actorList("next")! SendTicket(count-chunkSize)
		case MessageLoop(count:Int)=>
			Thread.sleep(50)
			var temp=count
			if(ticketNum==0&&count>0)
			{
				ticketNum+=chunkSize
				temp-=chunkSize
				println("Kiosk: Got one more ticket "+self)
				if(waitingBuyer.size!=0)
				{
					ticketNum-=1
					actorList("master") ! SuccessPurchaseTicket(waitingBuyer(0))
					waitingBuyer = waitingBuyer diff List(waitingBuyer(0))
				}
			}
			if(count==0&&remainTicketInTheLoop==true)
			{
				remainTicketInTheLoop=false
				println("Kiosk: No more tickets in current kiosk")
			}
			actorList("next")! MessageLoop(temp)
		case RequestMoreTicket(from:ActorRef,buyer:ActorRef)=>
			if(requestForMoreTicket==false)
			{
				if(ticketNum>0)
				{
					println("KIOSK: Got ticket from "+self)
					ticketNum-=1
					from ! ReceiveMoreTicket(from,buyer)
				}
				else
				{
					println("KIOSK: Ask next Kiosk to get tickets.")
					requestForMoreTicket=true
					actorList("next") ! RequestMoreTicket(from,buyer)
				}
			}
			else
			{
				actorList("master") ! FailPurchaseTicket(buyer)
			}
		case ReceiveMoreTicket(from:ActorRef,buyer:ActorRef)=>
			actorList("master") ! SuccessPurchaseTicket(buyer)
		case PurchaseTicket(act:ActorRef)=>//only if the ticket number is larger that one
			if(ticketNum>=1)
			{
				ticketNum-=1
				println("KIOSK: Got a ticket, remain "+ticketNum + "ticket(s)")
				sender ! SuccessPurchaseTicket(act)
			}
			else
			{
				if(remainTicketInTheLoop==true)//have ticket in the loop
				{
					//make the buyer into the list 
					waitingBuyer=act::waitingBuyer
					Thread.sleep(500)
				}
				else//don't have ticket in the loop, need to ask for tickets
				{
					//print("Kiosk: "+MoreTicket)
					if(ticketsInOther==true)//There some other tickets in other kiosks
					{
						requestForMoreTicket=true
						actorList("next")!RequestMoreTicket(self,act)
						println("Kiosk: Don't have ticket any more, request for more tickets.")
					}
					else
					{
						sender ! FailPurchaseTicket(act)
					}
				}
			}

	}
}
