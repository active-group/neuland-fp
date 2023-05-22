// Haustiere
// Ein Haustier ist eins der folgenden:
// - Hund -ODER-
// - Katze -ODER-
// - Schlange
enum Pet {
  case Dog
  case Cat
  case Snake
}

// Ist ein Haustier niedlich
// def: definiert eine Funktion
def isCute(pet: Pet): Boolean =
  pet match {
    case Pet.Dog => true
    case Pet.Cat => true
    case Pet.Snake => false
  }