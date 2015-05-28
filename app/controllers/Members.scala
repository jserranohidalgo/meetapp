package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.Play.current
import play.api.db.slick.DB

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import play.api.libs.concurrent.Akka

import org.hablapps.meetup.{domain, db, logic}, 
  logic._,
  db._,
  domain._

object Members extends Controller{

  def add(gid: Int): Action[Int] =
    Action(parse.json[Int]) { 
      fromHTTP(gid) andThen 
      join          andThen
      interpreter   andThen
      toHTTP
    }

  def interpreter[U]: Store[U] => Either[StoreError, U] = 
    MySQLInterpreter.run[U]
  
  def fromHTTP(gid: Int): Request[Int] => JoinRequest = 
    request => JoinRequest(None, request.body, gid)

  def toHTTP(response: Either[StoreError, Either[JoinRequest, Member]]): Result = 
    response fold(
      error => error match {
        case error@NonExistentEntity(id) => 
          NotFound(s"${error.msg}")
        case error@ConstraintFailed(IsMember(_,_,_)) => 
          Forbidden(s"${error.msg}")
        case error@ConstraintFailed(IsPending(_,_,_)) => 
          Forbidden(s"${error.msg}")
        case error => 
          InternalServerError(error.msg)
      },
      success => success fold(
        joinRequest => 
          Accepted(s"Join request $joinRequest, left pending for futher processing"),
        member => 
          Created(Json.toJson(member)(Json.writes[Member]))
      )
    )

}