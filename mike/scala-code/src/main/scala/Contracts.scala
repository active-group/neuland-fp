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

  case class Date(iso: String) {
    def greaterEqual(other: Date) = this.iso >= other.iso
  }

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
    def invert: Direction =
      this match {
        case Long => Short
        case Short => Long
      }
  }

  import Contract.*

  val c1 = Beg(Currency.EUR)
  val c2 = More(c1, 10) // Ich  bekomme 10€ jetzt

  val zcb1 = Later(More(Beg(Currency.EUR), 100), Date("2023-12-24"))

  def zeroCouponBond(date: Date, amount: Amount, currency: Currency): Contract =
    Later(More(Beg(currency), amount), date)
  val zcb1a = zeroCouponBond(Date("2023-12-24"), 100, Currency.EUR)

  case class Payment(date: Date, direction: Direction, amount: Amount, currency: Currency) {
    def scale(factor: Amount): Payment =
      this.copy(amount = factor * this.amount)
    def invert = this.copy(direction = this.direction.invert)
  }

  // smart constructor
  def more(contract: Contract, value: Amount): Contract =
    contract match {
      case Zero => Zero
      case _ => More(contract, value)
    }

  // Zahlungen bis zum Datum
  def semantics(contract: Contract, today: Date): (Seq[Payment], Contract) =
    contract match {
      case Contract.Beg(currency) => (Seq(Payment(today, Direction.Long,1, currency)), Zero)
      case Contract.More(contract, value) => {
        val (payments, residualContract) = semantics(contract, today)
        (payments.map(_.scale(value)), more(residualContract, value))
      }
      case Contract.Later(contract, date) =>
        if (today.greaterEqual(date))
          semantics(contract, today)
        else
          (Seq.empty, Later(contract, date))
      case Contract.Two(contract1, contract2) => {
        val (payments1, residualContract1) = semantics(contract1, today)
        val (payments2, residualContract2) = semantics(contract2, today)
        (payments1 ++ payments2, Two(residualContract1, residualContract2))
      }

      case Contract.Give(contract) => {
        val (payments, residualContract) = semantics(contract, today)
        (payments.map(_.invert), Give(residualContract))
      }

      case Contract.Zero => (Seq.empty, Zero)
    }

  val cn = More(Two(Beg(Currency.EUR),
    Later(Beg(Currency.EUR), Date("2023-12-24"))), 100)
}