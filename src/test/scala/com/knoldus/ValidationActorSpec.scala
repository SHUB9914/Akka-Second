import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{CallingThreadDispatcher, EventFilter, ImplicitSender, TestActor, TestActorRef, TestKit, TestProbe}
import com.knoldus.{Booking, Customer, PurchaseActor, ValidationActor}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}



object ValidationActorSpec {
  val testSystem = {
    val config = ConfigFactory.parseString(
      """
        |akka.loggers = [akka.testkit.TestEventListener]
      """.stripMargin
    )
    ActorSystem("test-system", config)
  }
}
import ValidationActorSpec._


class ValidationActorSpec extends TestKit(testSystem) with WordSpecLike
  with BeforeAndAfterAll with MustMatchers with ImplicitSender {

  override protected def afterAll(): Unit = {
    system.terminate()
  }


  "com.knoldus.ValidationActor " must {
    "Reply with the same message it receives without ask" in {
      val probe = TestProbe()
      val ref = TestActorRef(new ValidationActor(probe.ref))

      probe.setAutoPilot(new TestActor.AutoPilot {
        def run(sender: ActorRef, msg: Any): TestActor.AutoPilot =
          msg match {
            case msg:String =>
              sender.tell(1, self)
              TestActor.KeepRunning

            case Booking =>
              testActor.tell(0, sender)
              TestActor.KeepRunning
          }
      })


      ref ! "plz book the mobile"
      expectMsg(0)
    }


    "send sorry msg when no mobile available " in {

      val probe = TestProbe()
      val ref = TestActorRef(new ValidationActor(probe.ref))

      probe.setAutoPilot(new TestActor.AutoPilot {
        def run(sender: ActorRef, msg: Any): TestActor.AutoPilot =
          msg match {
            case msg:String =>
              sender.tell(0, self)
              TestActor.KeepRunning
          }
      })


      val dispatcherId = CallingThreadDispatcher.Id
      val props = PurchaseActor.props(testActor).withDispatcher(dispatcherId)

      EventFilter.info(message = "sorry no mobile available", occurrences = 1)
        .intercept {
          ref ! "plz book the mobile"
        }
    }

  }
}
