package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.Play.current
import play.api.db.slick.DB

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import play.api.libs.concurrent.Akka

import com.hablapps.funplay._
import scalaz.~>

import org.hablapps.meetup.{domain, db, logic}, 
  logic._,
  db._,
  domain._

object MembersPure extends Controller{

  import Members.{fromHTTP, toHTTP => toHTTP_plain}

  implicit def interpreter(implicit ec: ExecutionContext) = new (Store ~> Future){
    def apply[A](store: Store[A]): Future[A] =
      MySQLInterpreter.run(store).fold(
        error => Future.failed[A](error),
        success => Future.successful(success)
      )
  }

  def toHTTPSuccess(response: JoinResponse): Result =
    toHTTP_plain(Right(response))

  def toHTTPFromFailure: PartialFunction[Throwable, Result] = {
    case error: StoreError => toHTTP_plain(Left(error))
  }

  implicit val default_ec: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext
 
  def add(gid: Int): Action[Int] =
    Action.pure(join)( // [Store, JoinRequest, JoinResponse, Int]
      parse.json[Int],
      fromHTTP(0),
      toHTTPSuccess,
      toHTTPFromFailure
    )

}