package repositories

import model.Post
import org.scalatest._

class PostRepositoryTest extends AsyncWordSpec with Matchers {

  val repo = new PostRepository

  "A Post Repository" when {

    "deleting a post" should {

      "perform 1 operation if the post exists" in {
        val id = 1
        for {
          beforeDelete <- repo.find(id)
          deleteOps <- repo.delete(id)
          afterDelete <- repo.find(id)
        } yield {
          beforeDelete.isDefined shouldBe true
          deleteOps shouldBe 1
          afterDelete.isDefined shouldBe false
        }
      }

      "perform 0 operations if the post doesn't exist" in {
        repo.delete(-1)
          .map(_ shouldBe 0)
      }
    }

    "updating a post" should {
      val updatedBody = "Updated!"

      "perform 1 operation if the post exists" in {
        val id = 2
        for {
          beforeUpdate <- repo.find(id)
          updateOps <- repo.update(id, Post(id, "ZyseMe", updatedBody))
          afterUpdate <- repo.find(id)
        } yield {
          beforeUpdate.map(_.body) should not be Some(updatedBody)
          updateOps shouldBe 1
          afterUpdate.map(_.body) shouldBe Some(updatedBody)
        }
      }

      "perform 0 operations if the post exists" in {
        val id = -1
        repo.update(id, Post(id, "ZyseMe", updatedBody))
          .map(_ shouldBe 0)
      }
    }
  }

}
