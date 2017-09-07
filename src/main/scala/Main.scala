import java.time.{Clock, LocalDate, ZoneOffset}
import java.util.Date

import io.getquill.{CassandraAsyncContext, MappedEncoding, SnakeCase}
import scala.concurrent.ExecutionContext.Implicits.global

case class Foo(d: LocalDate)

object Main {

  object Encoders {
    implicit val localDateEncoder: MappedEncoding[LocalDate, Date] =
      MappedEncoding[LocalDate, Date](localDate => Date.from(
        localDate.atStartOfDay().toInstant(ZoneOffset.UTC))
      )
    implicit val localDateDecoder: MappedEncoding[Date, LocalDate] =
      MappedEncoding[Date, LocalDate](_.toInstant.atZone(Clock.systemDefaultZone().getZone).toLocalDate)

  }


  lazy val ctx = new CassandraAsyncContext[SnakeCase]("ctx")

  import Encoders._
  import ctx._


  val q: Quoted[Query[Foo]] = quote {
    query[Foo]
  }
  ctx.run(q)

}
