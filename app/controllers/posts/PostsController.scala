package controllers.posts

import controllers.posts.PostsController._
import javax.inject.Inject
import model.exceptions.DuplicateIdException
import model.{DataResponse, MessageResponse, Post}
import play.api.Logging
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._
import repositories.PostRepository

import scala.concurrent.{ExecutionContext, Future}

class PostsController @Inject()(
                                 cc: ControllerComponents,
                                 postRepository: PostRepository,
                                 implicit val executionContext: ExecutionContext
                               ) extends AbstractController(cc) with Logging {

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    val postResult = request.body.validate[Post]
    postResult.fold(
      errors => {
        Future.successful {
          BadRequest(Json.toJson(MessageResponse(BAD_REQUEST, Json.prettyPrint(JsError.toJson(errors)))))
        }
      },
      post => {
        postRepository.insert(post).map(persisted =>
          Created(Json.toJson(DataResponse(CREATED, persisted))).withHeaders((LOCATION, persisted.id.toString)))
          .recover {
            case e: DuplicateIdException =>
              logger.error(e.message)
              Conflict(e.message)
            case _ => InternalServerError
          }
      }
    )
  }

  def readAll(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    postRepository.findAll.map { posts =>
      Ok(Json.toJson(DataResponse(OK, posts.sortBy(_.id))))
    }.recover { case _ => InternalServerError }
  }

  def readSingle(id: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    postRepository.find(id).map {
      case Some(post) => Ok(Json.toJson(DataResponse(OK, post)))
      case _ => NotFound(Json.toJson(MessageResponse(NOT_FOUND, PostNotFoundMessage)))
    }.recover { case _ => InternalServerError }
  }

  def delete(id: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    postRepository.delete(id).map {
      case 0 => NotFound
      case _ => NoContent
    }.recover { case _ => InternalServerError }
  }

  def update(id: Int): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Post].fold(
      errors => {
        Future.successful {
          BadRequest(Json.toJson(MessageResponse(BAD_REQUEST, Json.prettyPrint(JsError.toJson(errors)))))
        }
      },
      post => {
        if (id == post.id) {
          postRepository.update(id, post).map {
            case 0 => NotFound
            case _ => NoContent
          }
        } else {
          Future.successful(BadRequest(IdMismatchMessage))
        }
      }.recover { case _ => InternalServerError }
    )
  }

}

object PostsController {
  val PostNotFoundMessage = "Post not Found"
  val IdMismatchMessage = "The resource id must match that of the id parameter"
}
