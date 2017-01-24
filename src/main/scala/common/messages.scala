package common
import akka.actor.ActorRef

case class Message(title: String, url: String)
case class Init(master:ActorRef)
case object ACK
case object InitRing
case class MakeRing(next:ActorRef)
case object RACK
case class SendTicket(count:Int)
case class InitSendTicket(ticketNum:Int)
case class MessageLoop(ticketNum:Int)
case class PurchaseTicket(act:ActorRef)
case class ClientPurchaseTicket(choice:Int)
case class RequestKiosk(choice:Int)
case class SuccessPurchaseTicket(act:ActorRef)
case object Done
case class RequestMoreTicket(from:ActorRef,buyer:ActorRef)
case class ReceiveMoreTicket(from:ActorRef,buyer:ActorRef)
case class FailPurchaseTicket(act:ActorRef)
case class WaitLoopToBuyTicket(act:ActorRef)
