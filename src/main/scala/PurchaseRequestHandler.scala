import akka.actor.{Actor, ActorLogging, Props}

class PurchaseRequestHandler extends Actor with ActorLogging{
  var totalMobile = 1
  override def receive = {
    case totalMobiles:String => sender ! totalMobile

    case Booking  => totalMobile -=1
      log.info("your booking successfully done!!!!")
  }
}

object PurchaseRequestHandler{
  def props  = Props(classOf[PurchaseRequestHandler])
}



