import slick.dbio.{DBIOAction, NoStream}

import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Demo {

  val db = Database.forConfig("h2mem1")

  // The query interface for the Suppliers table
  val suppliers: TableQuery[Suppliers] = TableQuery[Suppliers]

  // the query interface for the Coffees table
  val coffees: TableQuery[Coffees] = TableQuery[Coffees]

  val setupAction: DBIO[Unit] = DBIO.seq(
    // Create the schema by combining the DDLs for the Suppliers and Coffees
    // tables using the query interfaces
    (suppliers.schema ++ coffees.schema).create,

    // Insert some suppliers
    suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
    suppliers += (49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
    suppliers += (150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966")
  )

  val insertCoffeesAction: DBIO[Option[Int]] = coffees ++= Seq (
    ("Colombian",         101, 7.99, 0, 0),
    ("French_Roast",       49, 8.99, 0, 0),
    ("Espresso",          150, 9.99, 0, 0),
    ("Colombian_Decaf",   101, 8.99, 0, 0),
    ("French_Roast_Decaf", 49, 9.99, 0, 0)
  )

  def setup: Future[Unit] =
    for {
      _ <- execute(setupAction)
      _ <- execute(insertCoffeesAction)
    } yield {
      ()
    }

  def execute[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] = {
    val future = db.run(a)
    future.onComplete{
      case Success(result) => println; println(result)
      case Failure(t) => t.printStackTrace()
    }
    future
  }

}
