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
      case Nil => ???
      case first :: rest => ???
    }
}