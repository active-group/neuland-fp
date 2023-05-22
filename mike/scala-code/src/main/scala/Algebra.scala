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
    def combine(a1: A, a2: A): A
  }

  enum MyBool {
    case Yo
    case No
  }

  val myBoolSemigroup = Semigroup[MyBool] {
    override def combine(a1: MyBool, a2: MyBool): MyBool =
      (a1, a2) match {
        case (MyBool.Yo, MyBool.Yo) => MyBool.Yo
        case _ => MyBool.No
      }
  }
}