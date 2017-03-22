import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActor, TestActorRef, TestKit, TestProbe}
import com.knoldus.{Booking, ValidationActor}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}


class ValidationActorSpec extends TestKit(ActorSystem("test-system")) with WordSpecLike
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
  }
}
