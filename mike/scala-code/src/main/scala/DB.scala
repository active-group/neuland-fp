import scala.annotation.tailrec

object DB {
  /*
  put("Mike", 52)
  x = get("Mike")
  put("Mike", x+1)
  y = get("Mike")
  return (x+y).toString
  */
  type Key = String
  type Value = Int

  /*
  enum DBCommand[A] {
    case Put(key: Key, value: Value)
    case Get(key: Key)
    case Return(result: A)
  }
  type DBProgram[A] = List[DBCommand[A]]

  import DBCommand.*
  val p1 = List(Put("Mike", 52),
                     Get("Mike"),
                     Put("Mike", x+1))
*/
  enum DB[A] {
    case Get(key: Key,               callback: Value => DB[A])
    case Put(key: Key, value: Value, callback: Unit  => DB[A])
    case Return(result: A)

    def flatMap[B](fdb: A => DB[B]): DB[B] = splice(this, fdb)
    def map[B](f: A => B): DB[B] =
      this.flatMap { a => Return(f(a)) }
  }

  import DB.*

  val p1 = Put("Mike", 52, { _ =>
           Get("Mike", { x =>
             Put("Mike", x+1, { _ =>
               Get("Mike", { y =>
                 Return((x+y).toString)
               })
             })
           })
  })

  def get(key: Key): DB[Value] = Get(key, Return)
  def put(key: Key, value: Value): DB[Unit] = Put(key, value, Return)

  def splice[A, B](dba: DB[A], fdb: A => DB[B]): DB[B] =
    dba match {
      case Get(key, callback) =>
        Get(key, { value =>
          splice(callback(value), fdb)
        })
      case Put(key, value, callback) =>
        Put(key, value, { _ =>
          splice(callback(()), fdb)
        })
      case Return(result) => fdb(result)
    }

  val p1a = for {
    _ <- put("Mike", 52)
    x <- get("Mike")
    _ <- put("Mike", x+1)
    y <- get("Mike")
  } yield (x+y).toString

  @tailrec
  def runDB[A](db: DB[A], map: Map[Key, Value]): (Map[Key, Value], A) =
    db match {
      case Get(key, callback) =>
        runDB(callback(map.get(key).get), map)
      case Put(key, value, callback) =>
        runDB(callback(()), map.updated(key, value))
      case Return(result) => (map, result)
    }

}
