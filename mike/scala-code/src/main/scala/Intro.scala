// Haustiere
// Ein Haustier ist eins der folgenden:
// - Hund -ODER-
// - Katze -ODER-
// - Schlange
/*
enum Pet {
  case Dog
  case Cat
  case Snake
  case Camel

  def isCute(): Boolean =
    this match {
      case Dog => true
      case Cat => true
      case Snake => false
      case Camel => false
    }
}
*/

sealed trait Pet { // "interface"
  // "funktionale Methode" ohne Argumente: keine Klammern
  def isCute: Boolean
}
// "companion object"
object Pet {
  object Dog extends Pet {
    override def isCute = true
  }
  object Cat extends Pet {
    override def isCute: Boolean = true
  }
  object Snake extends Pet {
    override def isCute: Boolean = false
  }
  object Camel extends Pet {
    override def isCute: Boolean = false
  }

  // Ist ein Haustier niedlich
  // def: definiert eine Funktion
  def isCute(pet: Pet): Boolean =
    pet match {
      case Pet.Dog => true
      case Pet.Cat => true
      case Pet.Snake => false
      case Pet.Camel => false
    }

  val p1 = Dog isCute
}



