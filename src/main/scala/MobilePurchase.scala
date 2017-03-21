import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import scala.concurrent.duration.DurationInt
import akka.routing.FromConfig
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import scala.concurrent.{Await, Future}
case class TotalMobile(mobiles:Int)
case object Booking
case class Customer(name:String,address:String,creditCardNo:Long,mobileNo:Long,noOfMobile : Int)


class PurchaseRequestHandler extends Actor with ActorLogging{
  var totalMobile = 1
  override def receive = {
    case totalMobiles:String => sender ! totalMobile

    case Booking  => totalMobile -=1
      log.info("your booking succesfully done!!!!")
  }
}

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

class PurchaseActor(validationActorRef:ActorRef) extends Actor with ActorLogging {
  override def receive = {

    case customer:Customer =>if(customer.noOfMobile > 1){
      log.info("sorry you can take one mobile at a time!!!!!!!!!")
    }
    else {
      log.info(s"purchaseActor that sends request to validateActor ")
      validationActorRef ! "plz booking"
    }
  }
}

object MobilePurchase extends App{

  val config = ConfigFactory.parseString(
    """
      |akka.actor.deployment {
      | /poolRouter {
      |   router = balancing-pool
      |   nr-of-instances = 5
      | }
      |}
    """.stripMargin
  )
  val system = ActorSystem("PurchesActor",config)
  val PurchaseActorRef = system.actorOf(Props[PurchaseRequestHandler])
  val ValidationActorRef = system.actorOf(Props(new ValidationActor(PurchaseActorRef)))
  val ref = system.actorOf(FromConfig.props(Props(new PurchaseActor(ValidationActorRef))),"poolRouter")

  val user1 = Customer("shubham","noida":String,123456,123456789,1)
  val user2 = Customer("rahul","noida":String,123456,123456789,1)

  ref ! user1
  ref ! user2
  ref ! user1
  ref ! user2



}
