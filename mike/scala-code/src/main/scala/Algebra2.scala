object Algebra2 {
  sealed trait MyBool extends Semigroup[MyBool] {
    def und(b2: MyBool): MyBool

    def oder(b2: MyBool): MyBool
  }

  case object Yo extends MyBool {
    override def und(b2: MyBool): MyBool = b2

    override def oder(b2: MyBool): MyBool = Yo

    override def combine(a2: MyBool): MyBool = a2
  }

  case object No extends MyBool {
    override def und(b2: MyBool): MyBool = No

    override def oder(b2: MyBool): MyBool = b2

    override def combine(a2: MyBool): MyBool = No
  }

  trait Semigroup[A] {
    def combine(a2: A): A
  }

  // def listMap[A, B](f: A => B, list: List[A]): List[B]
  // def optionMap[A, B](f: A => B, list: Option[A]): Option[B]

  // soll drankleben an List[A] bzw. Option[A]
  trait Functor[F[_]] {
    // def map[A, B](thing: F[A], f: A => B): F[B]
    extension [A, B](thing: F[A])
      def map(f: A => B): F[B]
  }

  sealed trait Maybe[+A] { // GEHT NICHT: extends Functor[Maybe]
    def map[B](f: A => B): Maybe[B]
  }
  case object Null extends Maybe[Nothing] {
    override def map[B](f: Nothing => B): Maybe[B] = Null
  }
  case class Just[+A](value: A) extends Maybe[A] {
    override def map[B](f: A => B): Maybe[B] = Just(f(value))
  }
}
