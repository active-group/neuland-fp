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

  val list3 = 1 :: 2 :: 3 :: Nil
  val list4 = List(1,2,3,4,5)
}