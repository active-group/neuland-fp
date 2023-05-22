object Animals {
  // Ein Tier (auf dem texanischen Highway) ist eins der folgenden:
  // - Gürteltier -ODER-
  // - Papagei

  enum Animal {
    // Ein Gürteltier hat folgende Eigenschaften:
    // - lebendig oder tot
    // - Gewicht
    case Dillo(liveness: Liveness, weight: Weight)
    case Parrot(sentence: String, weight: Weight)
    // gemischte Daten / aus zusammengesetzten Daten

    def runOver: Animal =
      this match {
        case Dillo(_, weight) => Dillo(Liveness.Dead, weight)
        case Parrot(_, w) => Parrot("", w)
      }

    def feed(amount: Weight): Animal = {
      import Liveness.*
      this match {
        case Dillo(Dead, _) => this
        case Dillo(Alive, weight) => Dillo(Alive, weight+amount)
        case Parrot(sentence, weight) => Parrot(sentence, weight+amount)
      }
    }
  }

  import Animal.*

  val dillo1 = Dillo(Liveness.Alive, 10)
  val dillo1fed = dillo1 feed 5

  import Animal.*

  def runOver(animal: Animal): Animal = {
    import Animal.*
    animal match {
      case Dillo(_, weight) => Dillo(Liveness.Dead, weight)
      case Parrot(_, w) => Parrot("", w)
    }
  }

  // Aufgabe: Funktion schreiben, die ein Tier füttert.
  // Tote Gürteltiere nehmen nicht zu.

  type Weight = Double

  // "Lebendigkeit" ist eins der folgenden:
  // - lebendig
  // - tot
  enum Liveness {
    case Alive
    case Dead
  }

}