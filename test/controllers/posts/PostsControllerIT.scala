package controllers.posts

import model.{MessageResponse, Post}
import org.scalatest.{Matchers, TestSuite, WordSpec}
import org.scalatestplus.play.WsScalaTestClient
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.HeaderNames.LOCATION
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.test.Helpers.{await, defaultAwaitTimeout}


class PostsControllerIT extends WordSpec with TestSuite with Matchers with GuiceOneServerPerSuite with WsScalaTestClient {

  implicit val ws: WSClient = app.injector.instanceOf(classOf[WSClient])

  "A PostsController" when {

    "Reading all posts" should {

      val getAllUrl = "/api/v1/posts"

      "return the Posts in ascending order on the ids" in {
        val response = await(wsUrl(getAllUrl).get())
        val posts = Json.parse(response.body).as[Seq[Post]]

        response.status shouldBe 200
        posts match {
          case head +: second +: _ =>
            head.id shouldBe 1
            second.id shouldBe 2
        }
      }
    }

    "reading a single post" should {
      def getByIdUrl(id: Int = 1) = s"/api/v1/posts/$id"

      "return only the post with the matching id" in {
        val id = 1
        val response = await(wsUrl(getByIdUrl(id)).get())
        val post = Json.parse(response.body).as[Post]

        response.status shouldBe OK
        post.id shouldBe id
      }

      "returns a 404 with a json MessageResponse if the post does not exist" in {
        val response = await(wsUrl(getByIdUrl(-1)).get())
        val message = Json.parse(response.body).as[MessageResponse]

        response.status shouldBe NOT_FOUND
        message.status shouldBe NOT_FOUND
        message.message shouldBe PostsController.PostNotFoundMessage
      }
    }

    "creating a new post" should {
      def createUrl = "/api/v1/posts"

      "fail if there is already a Post with the same id present." in {
        val post = Post(1, "ZyseMe", "Test Post")
        val response = await(wsUrl(createUrl).post(Json.toJson(post)))
        response.status shouldBe CONFLICT
      }

      "save a post into the persistance layer returning the created Post and the id in the location header" in {
        val post = Post(999, "ZyseMe", "Test Post")
        val response = await(wsUrl(createUrl).post(Json.toJson(post)))
        val postResponse = Json.parse(response.body).as[Post]

        response.status shouldBe CREATED
        response.header(LOCATION) shouldBe Some(post.id.toString)
        postResponse shouldBe post
      }
    }

    "deleting a post" should {
      def deleteUrl(id: Int) = s"/api/v1/posts/$id"

      "return NO_CONTENT if post exists" in {
        val response = await(wsUrl(deleteUrl(1)).delete())

        response.status shouldBe NO_CONTENT
      }
      "return NOT_FOUND if post doesn't exist" in {
        val response = await(wsUrl(deleteUrl(-1)).delete())

        response.status shouldBe NOT_FOUND
      }
    }

    "updating a post" should {
      def updateUrl(id: Int) = s"/api/v1/posts/$id"

      "not allow ids to be changed" in {
        val post = Post(1, "ZyseMe", "Test Post")
        val response = await(wsUrl(updateUrl(2))
          .put(Json.toJson(post)))

        response.status shouldBe BAD_REQUEST
      }

      "update the post with the given id" in {
        val post = Post(2, "ZyseMe", "Test Post")
        val response = await(wsUrl(updateUrl(2))
          .put(Json.toJson(post)))

        response.status shouldBe NO_CONTENT
      }

      "return NOT_FOUND if no post with the id exists" in {
        val post = Post(9999, "ZyseMe", "Test Post")
        val response = await(wsUrl(updateUrl(9999))
          .put(Json.toJson(post)))

        response.status shouldBe NOT_FOUND
      }
    }
  }

}
