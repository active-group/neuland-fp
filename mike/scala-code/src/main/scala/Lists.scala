import scala.annotation.tailrec

object Lists {
  // Eine Liste ist eins der folgenden:
  // - die leere Liste
  // - eine Cons-Liste aus erstem Element und Rest-Liste
  //                                               ^^^^^
  enum MyList[+A] {
    case Empty
    case Cons(first: A, rest: MyList[A])
  }

  // Eingebaute Listen:
  // - leere Liste: Nil
  // - Cons: :: (Infix)

  def listSum(list: List[Int]): Int =
    list match {
      case Nil => 0
      case first :: rest =>
        first + listSum(rest) // "first + []: Kontext des listSum-Aufrufs
    }

  // acc: enthält "die Summe der gesehenen Elemente"
  @tailrec
  def listSum1(list: List[Int], acc: Int = 0): Int =
    list match {
      case Nil => acc
      case first :: rest =>
        listSum1(rest, first + acc) // tail call, endrekursiver Aufruf
    }

  // auf der JVM:
  // - zur Laufzeit wird Kontext durch Stack repräsentiert
  // - Stack klein, feste Größe
  // - auch tail calls verbrauchen Stack-Platz


  // (cons 1 (cons 2 (cons 3 empty)))
  val list3 = 1 :: (2 :: (3 :: Nil))
  val list3michel = Nil.::(3).::(2).::(1)
  val list4 = List(1,2,3,4,5)

  def listMap[A, B](f: A => B, list: List[A]): List[B] =
    list match {
      case Nil => Nil
      case first :: rest =>
        f(first) :: listMap(f, rest)
    }

  def optionMap[A, B](f: A => B, o: Option[A]): Option[B] =
    o match {
      case None => None
      case Some(value) => Some(f(value))
    }


  def listMap2[A, B](f: A => B, list: List[A], acc: List[B]): List[B] =
    list match {
      case Nil => acc.reverse
      case first :: rest =>
        listMap2(f, rest, f(first) :: acc)
    }

  def inc(n: Int): Int = n +1

  val list4a = listMap(inc, list4)
  val list4b = listMap({ (n: Int) => n + 1 }, list4)
  val list4c = listMap(inc(_), list4)
  val list4d = list4.map(inc)
  val list4e = list4.map {n => n + 1}
  val list4f = list4.map(_+3)

  def listFold[A, B](z: B, f: (A, B) => B, list: List[A]): B =
    list match {
      case Nil => z
      case first :: rest =>
        f(first, listFold(z, f, rest))
    }

  val sum4 = listFold[Int, Int](0, {(a, b) => a + b}, list4)
  val sum4a = listFold[Int, Int](0, _+_, list4)

  def listFold2[A, B](list: List[A])(z: B, f: (A, B) => B): B =
    list match {
      case Nil => z
      case first :: rest =>
        f(first, listFold2(rest)(z, f))
    }

  val list4fold = listFold2[Int,Int](list4)
  val sum4b = listFold2(list4)(0, _+_)
  val sum4c = list4fold(0, _+_)

  // Haskell B. Curry / Moses Schönfinkel
  val sum4d = list4.foldRight(0)(_+_)

  def schönfinkeln[A, B, C](f: (A, B) => C): A => (B => C) =
    { (a: A) => { (b: B) => f(a, b) } }

  def entschönfinkeln[A, B, C](f: A => B => C): (A, B) => C =
    { (a: A, b: B) => f(a)(b) }

  // Aufgabe: Funktion schreiben, die die beiden Argumente einer
  // Funktion vertauscht

  /*
  enum Option[+A] {
    case None
    case Some(value: A)
  }
  */
  
  
  // Index eines Elements in einer Liste
  def listIndex[A](element: A, list: List[A]): Option[Int] =
    list match {
      case Nil => None
      case first :: rest =>
        if (first == element)
          Some(0)
        else
          listIndex(element, rest).map(_+1)
          /*
          listIndex(element, rest) match {
            case None => None
            case Some(index) =>
              Some(index+1)
          }
          */
           /* {
          val o = listIndex(element, rest)
          if (o.isInstanceOf[None.type])
            None
          else if (o.isInstanceOf[Some[Int]]) {
            val index = o.asInstanceOf[Some[Int]].value
            Some(index+1)
          } else
            throw Exception("can't happen")

        }
            */
    }
}