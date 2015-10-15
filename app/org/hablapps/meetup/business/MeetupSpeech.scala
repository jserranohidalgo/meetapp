// package org.hablapps.meetup.logic

// import play.api.Play.current
// import play.api.db.slick.DB

// import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
// import scala.slick.driver.MySQLDriver.simple._
// import org.hablapps.meetup.domain._
// import org.hablapps.meetup.db._, MySQLInterpreter._

// object Speech {

//   sealed trait Store[+U]

//   case class Get[T, U](id: Int, next: T => Store[U]) extends Store[U]
//   case class Put[T: Idable, U](t: T, next: T => Store[U]) extends Store[U]
//   case class Update[T: Idable, U](t: T, next: T => Store[U]) extends Store[U]
//   case class Delete[T, U](id: Int, next: T => Store[U]) extends Store[U]

//   case class PutRel[R: Rel, U](r: R, next: R => Store[U]) extends Store[U]
//   case class DelRel[R: Rel, U](r: R, next: R => Store[U]) extends Store[U]

//   case class Return[U](t: U) extends Store[U]
//   val Skip = Return(())
//   case class Fail(error: StoreError) extends Store[Nothing]

//   sealed class StoreError(val msg: String)

//   case class NonExistentEntity(id: Int) extends StoreError(s"Non-existent entity $id")
//   case class ConstraintFailed(constraint: Store[Boolean]) extends StoreError(s"Constraint failed: $constraint")
//   case class GenericError(override val msg: String) extends StoreError(msg)

//   def attempt(action: Store[A]): Store[Either[Unit,A]] = {
//     If(action.authorized)(
//       _then = action,
//       _else = Skip
//     }

//   def Say[T: SpeechAct](declaration: T): Store[T] = 
//     for {
//       d <- Put(declaration) 
//       _ <- PutRel((declaration.speaker, declaration))
//       _ <- PutRel((declaration.context, declaration))
//     } yield d

//   abstract class SpeechAct[S: Role, A: Role, C: Context, O]{
//     val speaker: R
//     val addressee: List[]
//     val context: C
//     def empowered: R => Action[Boolean]
//     def permitted: R => Action[Option[Boolean]]
//     val purpose: Action[O]
//   }

//   trait Join[A] extends SpeechAct[A]{

//   }

//   case class Join(agent: Agent, context: Interaction) extends SpeechAct[A]
//     for{
//       user <- Store.getUser(uid)
//       group <- Store.getGroup(gid)
//       joinOrMember <- 
//         Store.If(!group.must_approve)(
//           _then = Store.putMember(Member(None, uid, gid)), 
//           _else = Store.putJoin(request) unless Store.isPending(uid, gid)
//         ) unless Store.isMember(uid, gid)
//     } yield joinOrMember
//   }

// }
//  