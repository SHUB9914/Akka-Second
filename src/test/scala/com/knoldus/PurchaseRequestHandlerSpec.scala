import akka.actor.ActorSystem
import akka.testkit.{CallingThreadDispatcher, EventFilter, ImplicitSender, TestKit}
import com.knoldus.{Booking, PurchaseRequestHandler}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

object PurchaseRequestHandlerSpec {
  val testSystem = {
    val config = ConfigFactory.parseString(
      """
        |akka.loggers = [akka.testkit.TestEventListener]
      """.stripMargin
    )
    ActorSystem("test-system", config)
  }
}
import PurchaseRequestHandlerSpec._


class PurchaseRequestHandlerSpec extends TestKit(testSystem) with WordSpecLike
  with BeforeAndAfterAll with MustMatchers with ImplicitSender {

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  "A com.knoldus.PurchaseRequestHandler Actor " must {

    "send Booking confirmation msg " in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = PurchaseRequestHandler.props.withDispatcher(dispatcherId)
      val ref = system.actorOf(props)

      EventFilter.info(message = "your booking successfully done!!!!", occurrences = 1)
        .intercept {
          ref ! Booking
        }
    }
    "send total no of mobiles " in {

      val props = PurchaseRequestHandler.props
      val ref = system.actorOf(props)
        ref ! "give me total no of mobiles"

      expectMsg(1)

    }
  }
}
