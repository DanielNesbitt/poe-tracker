package fetcher

import java.nio.file.Paths
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshal}
import akka.stream.scaladsl.FileIO
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.Json

import scala.util.{Failure, Success}

/**
  * @author Daniel Nesbitt
  */
class RequestActor extends Actor with ActorLogging {

  import akka.pattern.pipe
  import context.dispatcher

  final implicit val marshaller: FromEntityUnmarshaller[Json] = ErrorAccumulatingCirceSupport.jsonUnmarshaller

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val http = Http(context.system)

  override def preStart(): Unit = {
    http.singleRequest(HttpRequest(uri = "http://www.pathofexile.com/api/public-stash-tabs?id=220-1652-744-1341-230"))
      .pipeTo(self)
  }

  override def receive: Receive = {
    case HttpResponse(StatusCodes.OK, headers, entity, _) => {
      val time = System.currentTimeMillis()
      val path = Paths.get("C:\\Users\\Daniel\\IdeaProjects\\Foo.json")
      val sink = FileIO.toPath(path)
      entity.dataBytes.runWith(sink).onComplete {
        case Success(_) => log.info("Wrote to file.")
        case Failure(th) => log.error(th, "Failed to write to file.")
      }
//      Unmarshal(entity)
//        .to[Json]
//        .onComplete {
//          case Success(json) => {
//            val changeIdField = json \\ "next_change_id"
//            changeIdField.headOption match {
//              case Some(field) =>
//                log.info("Fetched next_change_id of: " + field.toString() + " in " + (System.currentTimeMillis() - time) + " milliseconds; parsed " + humanReadableByteCount(entity.contentLengthOption.get))
//              case None =>
//                log.warning("No next_change_id specified.")
//            }
//          }
//          case Failure(th) => log.error(th, "Failed to un-marshall response.")
//        }

    }
    case resp@HttpResponse(code, _, _, _) => {
      log.info("Request failed, response code, " + code)
      resp.discardEntityBytes()
    }
  }

}
