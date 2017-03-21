import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration.DurationInt


import scala.concurrent.{Await, Future}

class ValidationActor(PurchaseRequestRef : ActorRef) extends Actor with ActorLogging {
  var number = 0
  override def receive = {

    case validate:String =>
      implicit val timeout = Timeout(1000 seconds)
      val totalmobile: Future[Int] = (PurchaseRequestRef ? "give me totalMobiles").mapTo[Int]
      number= Await.result(totalmobile,1.second)
      log.info("number of Mobiles="+number)
      if(number > 0) PurchaseRequestRef ! Booking else log.info("sorry no mobile available")

  }
}
