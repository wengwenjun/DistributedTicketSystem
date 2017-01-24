package ticket
import akka.actor.{Actor,ActorRef,Props,ActorSystem,OneForOneStrategy,Terminated}
import com.typesafe.config.ConfigFactory
import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}
import akka.remote._
import common._


class TicketMaster extends Actor{
	override val supervisorStrategy= OneForOneStrategy(){
		case _: IllegalArgumentException=> Resume
		case _: ArithmeticException	=> Stop
		case _: Exception		=> Restart
	}
	println("[TicketMaster] "+self)
	var numberofKiosks=ConfigFactory.load.getInt("numberofKiosks")
	var chunkSize=ConfigFactory.load.getInt("chunkSize")
	var totalTickets=ConfigFactory.load.getInt("totalTickets")
	var kioskList=List[ActorRef]()
	val system = ActorSystem("masterKiosk")
	var messageInTheLoop=true
	var i = 0
	for (i <- 1 to numberofKiosks){
		kioskList=system.actorOf(Props[Kiosk])::kioskList
	}
	//then I should sent the first message to all the actors of the kiosk
	println(kioskList)
	for (i <- 1 to numberofKiosks){
		kioskList(i-1) ! Init(self)
	}
	var ackCount=0
	var rackCount=0
	def receive={
		case ACK	=>
			ackCount+=1
			println("MASTER: Got one ACK")
			if(ackCount==numberofKiosks){
				println("Init: Got the all acks from all the kiosks.")
				println("Ring: Begin to make the ring.")
				self ! InitRing
			}
		case InitRing	=>			
			for (i <- 1 to chunkSize-1){
				kioskList(i-1) ! MakeRing(kioskList(i))
			}
			kioskList(chunkSize-1) ! MakeRing(self)
		case RACK	=>
			rackCount+=1	
			if(rackCount==numberofKiosks){
				println("Ring: Make the ring compelete.")
				self ! InitSendTicket(numberofKiosks*chunkSize)
				totalTickets-=(numberofKiosks*chunkSize)
			}
		case InitSendTicket(ticketNum:Int)=>
			kioskList(0) ! SendTicket(ticketNum)
		case RequestMoreTicket(from:ActorRef,buyer:ActorRef)=>
			kioskList(0) ! RequestMoreTicket(from,buyer)
		case SendTicket(ticketNum:Int)=>
			println("Master: Loop ticket back.")
			self ! MessageLoop(totalTickets)
		case MessageLoop(ticketNum:Int)=>
			kioskList(0) ! MessageLoop(ticketNum)	
			if(ticketNum==0&&messageInTheLoop==true)
			{
				messageInTheLoop=false
				println("Master: No more messages in current kiosk.")
			}
		case RequestKiosk(index:Int)=>
			println("Send the kios Request")
			context.watch(sender)
			kioskList(index) ! PurchaseTicket(sender)
			println("Master: send request ")
		case SuccessPurchaseTicket(act:ActorRef)=>
			act ! SuccessPurchaseTicket(self)
		case FailPurchaseTicket(act:ActorRef)=>
			act ! FailPurchaseTicket(self)
		case Terminated(_) =>
			println("Master: #########Thank you#######Disconnected#######.")
	}

}
