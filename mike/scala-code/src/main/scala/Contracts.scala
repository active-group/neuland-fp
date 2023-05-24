object Contracts {
  /*
  - einfaches Beispiel abfragen

  "Zero-Coupon Bond" / "Zero Bond"
  "Ich bekomme Weihnachten 100€."

  - Beispiel zerschlagen, in "atomare Bausteine" / "Ideen"

  - "Währung": Ich bekomme 1€ jetzt.
  - "Betrag": Ich bekomme 10€ jetzt.
  - "Später"

  - Fx Swap
  "Weihnachten: Ich bekomme 100€ -UND- ich zahle $100."
  */

  case class Date(iso: String)

  type Amount = Double

  enum Currency {
    case EUR
    case USD
    case YES
    case GBP
  }

  enum Contract {
    // case ZeroCouponBond(date: Date, amount: Amount, currency: Currency)
    // "Ich bekomme 1€ jetzt"
    case Beg(currency: Currency)
    case More(contract: Contract, value: Amount)
    case Later(contract: Contract, date: Date)
    case Two(contract1: Contract, contract2: Contract)
    case Give(contract: Contract)
    case Zero
  }

  enum Direction {
    case Long
    case Short
  }

  import Contract.*

  val c1 = Beg(Currency.EUR)
  val c2 = More(c1, 10) // Ich  bekomme 10€ jetzt

  val zcb1 = Later(More(Beg(Currency.EUR), 100), Date("2023-12-24"))

  def zeroCouponBond(date: Date, amount: Amount, currency: Currency): Contract =
    Later(More(Beg(currency), amount), date)
  val zcb1a = zeroCouponBond(Date("2023-12-24"), 100, Currency.EUR)

  case class Payment(date: Date, direction: Direction, amount: Amount, currency: Currency)

  // Zahlungen bis zum Datum
  def semantics(contract: Contract, today: Date): (Seq[Payment], Contract) = ???
}