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
        first + listSum(rest)
    }

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
}