package dnesbitt.fetcher

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshal}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import cats.Show
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe._
import io.circe.generic.semiauto._
import dnesbitt.json.Stashes

import scala.util.{Failure, Success}

/**
  * @author Daniel Nesbitt
  */
class RequestActor extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher
  import dnesbitt.json.PoeEntities._

  final implicit val errorShow: Show[Error] = Error.showError

  final implicit val marshaller: FromEntityUnmarshaller[Stashes] = FailFastCirceSupport.unmarshaller

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val http = Http(context.system)

  var changeId = ""

  override def preStart(): Unit = {
    http.singleRequest(HttpRequest(uri = "http://www.pathofexile.com/api/public-stash-tabs?id=220-1652-744-1341-230"))
      .pipeTo(self)
  }

  override def receive: Receive = {
    case HttpResponse(StatusCodes.OK, _, entity, _) => {
      Unmarshal(entity)
        .to[Stashes]
        .onComplete {
          case Success(stashes) => {
            log.info(s"Next change id ${stashes.next_change_id}")
            changeId = stashes.next_change_id
          }
          case Failure(th: Error) =>
            log.error("Failed to un-marshall response. " + errorShow.show(th))
        }

    }
    case resp@HttpResponse(code, _, _, _) => {
      log.info("Request failed, response code, " + code)
      resp.discardEntityBytes()
    }
  }

}
