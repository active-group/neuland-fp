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

  sealed trait MyBool {
    case Yo
    case No
  }
}