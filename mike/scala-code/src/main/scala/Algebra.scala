object Algebra {
  // Assoziativität
  // (a + b) + c = a + (b + c)
  // (a * c) * c = a * (b * c)

  // Halbgruppe:
  // Typ A
  // Operation(en):
  // def f(a1: A, a2: A): A
  // Gleichungen:
  // f(f(a, b), c) = f(a, f(b, c))

  trait Semigroup[A] {
    // combine muß assoziativ sein
    // 1. Fassung
    // def combine(a1: A, a2: A): A
    extension (a1: A)
      def combine(a2: A): A
  }

  enum MyBool {
    case Yo
    case No
  }

  val myBoolAndSemigroup = new Semigroup[MyBool] {
    /* 1. Fassung
    override def combine(a1: MyBool, a2: MyBool): MyBool =
      (a1, a2) match {
        case (MyBool.Yo, MyBool.Yo) => MyBool.Yo
        case _ => MyBool.No
      }
    */
    extension (a1: MyBool)
      def combine(a2: MyBool): MyBool =
        (a1, a2) match {
          case (MyBool.Yo, MyBool.Yo) => MyBool.Yo
          case _ => MyBool.No
        }
  }

  // 1. Fassung
  // val b1 = myBoolAndSemigroup.combine(MyBool.Yo, MyBool.No)
  import myBoolAndSemigroup.*
  val b1 = MyBool.Yo.combine(MyBool.No)

  def combineAll[A](list: List[A], semigroup: Semigroup[A]) = {
    import semigroup.combine
    list match {
      case Nil => throw Exception("must not happen")
      case first :: second :: Nil =>
        first.combine(second)
      case first :: rest =>
        first.combine(combineAll(rest, semigroup))
    }
  }
}