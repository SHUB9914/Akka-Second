import akka.actor.{ ActorSystem, Props}
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory


case class TotalMobile(mobiles:Int)
case object Booking
case class Customer(name:String,address:String,creditCardNo:Long,mobileNo:Long,noOfMobile : Int)



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
