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

  // Typklasse
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

  given myBoolAndSemigroup: Semigroup[MyBool] = new Semigroup[MyBool] {
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

  /*
  def combineAll[A](list: List[A])(using semigroup: Semigroup[A]): A = {
    import semigroup.combine
    list match {
      case Nil => throw Exception("must not happen")
      case first :: second :: Nil =>
        first.combine(second)
      case first :: rest =>
        first.combine(combineAll(rest))
    }
  }
  */
  def combineAll[A : Semigroup](list: List[A]): A = {
    list match {
      case Nil => throw Exception("must not happen")
      case first :: second :: Nil =>
        first.combine(second)
      case first :: rest =>
        first.combine(combineAll(rest))
    }
  }

  // Halbgruppe (A, combine) + neutrales Element
  // neutral : A
  // neutral.combine(x) == x.combine(neutral) == x
  // Monoid
  trait Monoid[A] extends Semigroup[A] {
    def neutral: A
  }

  given myBoolAndMonoid : Monoid[MyBool] with {
    extension (a1: MyBool)
      def combine(a2: MyBool): MyBool =
        (a1, a2) match {
          case (MyBool.Yo, MyBool.Yo) => MyBool.Yo
          case _ => MyBool.No
        }
    override def neutral: MyBool = MyBool.Yo
  }

  import MyBool.*
  val bb1 = combineAll(List(Yo, No, Yo, Yo))
  
  given tupleMonoid[A : Monoid, B : Monoid]: Monoid[(A, B)] with {
    override def neutral: (A, B) = (summon[Monoid[A]].neutral, summon[Monoid[B]].neutral)
    extension (t1: (A, B))
      def combine(t2: (A, B)): (A, B) = {
        val (a1, b1) = t1
        val (a2, b2) = t2
        (a1.combine(a2), b1.combine(b2))
      }
  }
}