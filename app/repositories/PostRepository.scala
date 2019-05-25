package repositories

import com.google.inject.Inject
import model.Post
import model.exceptions.DuplicateIdException
import play.api.Logging

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

/**
  * A "persistance" layer for the [[Post]]
  */
class PostRepository @Inject()(
                                implicit val executionContext: ExecutionContext
                              ) extends Logging {

  /**
    * This is the place where all posts are stored. You may change the type, but stick to solution form the
    * scala-std-library.
    */
  private val posts: ListBuffer[Post] = ListBuffer(
    Post(1, "Title 1", "Body 1"),
    Post(2, "Title 2", "Body 2")
  )

  def find(id: Int): Future[Option[Post]] = Future.successful(posts.find(_.id == id))

  def findAll: Future[Seq[Post]] = Future.successful(posts)

  def insert(post: Post): Future[Post] = posts.find(_.id == post.id).map(_ => {
    logger.trace(s"Duplicate Id for post: $post")
    Future.failed(new DuplicateIdException)
  }).getOrElse {
    posts += post
    Future.successful(post)
  }

  def delete(id: Int): Future[Int] = Future.successful(posts.find(_.id == id).foldLeft(0)((acc, post) => {
    logger.trace(s"Deleting post: $post")
    posts -= post
    acc + 1
  }))

  def update(id: Int, post: Post): Future[Int] = Future.successful {
    posts.find(_.id == id).foldLeft(0)((acc, existingPost) => {
      logger.trace(s"Updating post: $existingPost with new values $post")
      posts -= existingPost
      posts += post
      acc + 1
    })
  }

}
