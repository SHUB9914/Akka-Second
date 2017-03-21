import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class PurchaseActor(validationActorRef:ActorRef) extends Actor with ActorLogging {
  override def receive = {

    case customer:Customer =>if(customer.noOfMobile > 1){
      log.info("sorry you can take one mobile at a time!!!!!!!!!")
    }
    else {
      log.info(s"purchaseActor that sends request to validateActor ")
      validationActorRef ! "plz book"
    }
  }
}

object PurchaseActor{
  def props(ref: ActorRef)  = Props(classOf[PurchaseActor], ref)
}
