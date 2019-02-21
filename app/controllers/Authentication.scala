package controllers

import play.api.mvc._
import play.api.mvc.Results._

import com.google.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object Authentication {

  class UserOptRequest[A](val usernameOpt: Option[String], val passwordOpt: Option[String], request: Request[A]) extends WrappedRequest[A](request)

  class UserActionBuilder @Inject() (val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
    extends ActionBuilder[UserOptRequest, AnyContent] with ActionTransformer[Request, UserOptRequest] {

    def transform[A](request: Request[A]) = Future.successful {
      val usernameOpt = request.session.get("username")
      val passwordOpt = request.session.get("password")
      new UserOptRequest(usernameOpt, passwordOpt, request)
    }
  }

  case class AuthenticatedUserRequest[A](val username: String, request: Request[A]) extends WrappedRequest[A](request)

  def UserAuthentication(implicit ec: ExecutionContext) = new ActionRefiner[UserOptRequest, AuthenticatedUserRequest] {

    def executionContext = ec

    def refine[A](userRequest: UserOptRequest[A]): Future[Either[Result, AuthenticatedUserRequest[A]]] = Future.successful {
      import userRequest._
      (usernameOpt, passwordOpt) match {
        case (Some("admin"), Some("123")) => Right(AuthenticatedUserRequest("admin", userRequest))
        case (Some("tester"), Some("456")) => Right(AuthenticatedUserRequest("tester", userRequest))
        case _ => Left(Forbidden)
      }
    }
  }

  def AdminAuthentication(implicit ec: ExecutionContext) = new ActionFilter[AuthenticatedUserRequest] {

    def executionContext = ec

    def filter[A](authUserRequest: AuthenticatedUserRequest[A]): Future[Option[Result]] = Future.successful {
      if (authUserRequest.username == "admin")
        None
      else
        Some(Forbidden)
    }
  }

  case class AuthenticatedUserAction[A](block: Request[A] => Future[Result])(implicit val executionContext: ExecutionContext, val parser: BodyParser[A], userActionBuilder: UserActionBuilder)
  extends Action[A] {

    def apply(request: Request[A]): Future[Result] = {
      userActionBuilder andThen UserAuthentication invokeBlock (request, block)
    }
  }
  
  case class AdminAction[A](block: Request[A] => Future[Result])(implicit val executionContext: ExecutionContext, val parser: BodyParser[A], userActionBuilder: UserActionBuilder)
  extends Action[A] {

    def apply(request: Request[A]): Future[Result] = {
      userActionBuilder andThen UserAuthentication andThen AdminAuthentication invokeBlock (request, block)
    }
  }
}