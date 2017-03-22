import akka.actor.{ActorSystem, Props}
import akka.testkit.{CallingThreadDispatcher, EventFilter, TestKit}
import com.knoldus.{Customer, PurchaseActor}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}


object PurchaseActorSpec {
  val testSystem = {
    val config = ConfigFactory.parseString(
      """
        |akka.loggers = [akka.testkit.TestEventListener]
      """.stripMargin
    )
    ActorSystem("test-system", config)
  }
}
import PurchaseActorSpec._

  class PurchaseActorSpec extends TestKit(testSystem) with WordSpecLike
    with BeforeAndAfterAll with MustMatchers {

    override protected def afterAll(): Unit = {
      system.terminate()
    }

    "A com.knoldus.PurchaseActor Actor " must {
      "send a message to another actor when it finished processing" in {

        val ref = system.actorOf(PurchaseActor.props(testActor))
        val user = Customer("shubham", "noida": String, 123456, 123456789, 1)

        ref ! user
        expectMsg("plz book")

      }


      "send sorry msg when user make request more than 1 mobile at a time " in {
        val dispatcherId = CallingThreadDispatcher.Id
        val props = PurchaseActor.props(testActor).withDispatcher(dispatcherId)
        val ref = system.actorOf(props)
        val user = Customer("shubham", "noida": String, 123456, 123456789, 2)

        EventFilter.info(message = "sorry you can take one mobile at a time!!!!!!!!!", occurrences = 1)
          .intercept {
            ref ! user
          }
      }
    }
  }


