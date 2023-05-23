import cats.*
import cats.implicits.*
import cats.data.*
import AttributEntwurf.{AttributNichtDa, AttributUnzulässig}
import Warenkorb.WarenkorbBestellfertig

// NEIN:
// case class Artikel(kategorie: ArtikelKategorie, name: String, haartyp: Option[)

case class Artikel(kategorie: ArtikelKategorie, name: String)

enum Haartyp {
  case Schuppen
  case Fettig
  case Spliss
}

enum ArtikelKategorie {
  case Kosmetik
  case Shampoo(haartyp: Haartyp)
  case Mode
  case Möbel
  case Lebensmittel
}

trait Kunde

trait Adresse

enum Zahlungsart {
  case Paypal
  case Bitcoin
  case SEPA
}

enum GrundFürUnzulässigeLieferadresse {
  case Packstation
  case MieseGegend
}

enum GrundfürUnzulässigeZahlungsart {
  case Bitcoin
  case Schufa
  case PaypalNichtBeiKosmetik
}

enum LieferadressenEntwurf {
  case EineLieferadresse(adresse: Adresse)
  case UnzulässigeLieferadresse(adresse: Adresse,
                                grund: GrundFürUnzulässigeLieferadresse)
  case KeineLieferAdresse
}

enum AttributEntwurf[+T, +GRUND] {
  case AttributIstDa(wert: T)
  case AttributUnzulässig(wert: T, grund: Seq[GRUND])
  case AttributNichtDa

  def mapGrund[GRUND2](f: GRUND => GRUND2): AttributEntwurf[T, GRUND2] =
    this match {
      case AttributIstDa(wert) => AttributIstDa(wert)
      case AttributUnzulässig(wert, grund) => AttributUnzulässig(wert, grund.map(f))
      case AttributNichtDa => AttributNichtDa
    }
}

given attributEntwurfFunctor[GRUND]: Functor[AttributEntwurf[_, GRUND]] with {
  override def map[A, B](fa: AttributEntwurf[A, GRUND])(f: A => B): AttributEntwurf[B, GRUND] =
    fa match {
      case AttributIstDa(wert) => AttributIstDa(f(wert))
      case AttributUnzulässig(wert, grund) => AttributUnzulässig(f(wert), grund)
      case AttributNichtDa => AttributNichtDa
    }
}

given attributEntwurfApplicative[GRUND] : Applicative[AttributEntwurf[_, GRUND]] with {
  override def pure[A](x: A): AttributEntwurf[A, GRUND] =
    AttributIstDa(x)

  override def ap[A, B](ff: AttributEntwurf[A => B, GRUND])(fa: AttributEntwurf[A, GRUND])
  : AttributEntwurf[B, GRUND] =
    (ff, fa) match {
      case (AttributNichtDa, _) => AttributNichtDa
      case (_, AttributNichtDa) => AttributNichtDa
      case (AttributIstDa(f), AttributIstDa(a)) => AttributIstDa(f(a))
      case (AttributIstDa(f), AttributUnzulässig(a, grund)) =>
        AttributUnzulässig(f(a), grund)
      case (AttributUnzulässig(f, grund), AttributIstDa(a)) =>
        AttributUnzulässig(f(a), grund)
      case (AttributUnzulässig(f, grund1), AttributUnzulässig(a, grund2)) =>
        AttributUnzulässig(f(a), grund1 ++ grund2) // Halbgruppe wäre schön
    }
}

given attributEntwurfMonad[GRUND] : Monad[AttributEntwurf[_, GRUND]] with {
  override def pure[A](x: A): AttributEntwurf[A, GRUND] =
    AttributIstDa(x)

  override def flatMap[A, B](fa: AttributEntwurf[A, GRUND])(f: A => AttributEntwurf[B, GRUND])
    : AttributEntwurf[B, GRUND] =
    fa match {
      case AttributNichtDa => AttributNichtDa
      case AttributIstDa(wert) => f(wert)
      case AttributUnzulässig(wert, grund1) =>
        f(wert) match {
          case AttributNichtDa => AttributNichtDa
          case AttributUnzulässig(wert, grund2) => AttributUnzulässig(wert, grund1 ++ grund2)
          case AttributIstDa(wert) => AttributUnzulässig(wert, grund1)
        }
    }

  override def tailRecM[A, B](a: A)(f: A => AttributEntwurf[Either[A, B], GRUND]): AttributEntwurf[B, GRUND] = ???
}

//
// def map[A, B](fa: AttributEntwurf[A, GRUND])(f: A => B): AttributEntwurf[B, GRUND] =
// def map2[A, B, Z](fa: AttributEntwurf[A, GRUND], fb: AttributEntwurf[B, GRUND])(f: (A, B) => Z)
///     : AttributEntwurf[Z, GRUND]

// def foo = attributEntwurfApplicative.

import AttributEntwurf._

def attributEntwurfMap[A, B, GRUND](f: A => B, entwurf: AttributEntwurf[A, GRUND])
  : AttributEntwurf[B, GRUND]
  = entwurf match {
      case AttributIstDa(wert) => AttributIstDa(f(wert))
      case AttributUnzulässig(wert, grund) => AttributUnzulässig(f(wert), grund)
      case AttributNichtDa => AttributNichtDa
    }

trait Grußkarte

enum Warenkorb {
  case WarenkorbBestellfertig(artikel: Seq[Artikel],
                              kunde: Kunde,
                              lieferadresse: Adresse,
                              zahlungsart: Zahlungsart,
                              grußkarte: Option[Grußkarte]
                             )
  case WarenkorbEntwurf(artikel: Seq[Artikel],
                        kunde: Option[Kunde],
                        lieferadresse: AttributEntwurf[Adresse, GrundFürUnzulässigeLieferadresse],
                        zahlungsart: AttributEntwurf[Zahlungsart, GrundfürUnzulässigeZahlungsart],
                        grußkarte: Option[Grußkarte])

  def istBestellfertig: Bestellfertigkeit =
    this match {
      case _: WarenkorbBestellfertig => Bestellfertigkeit.Bestellfertig
      case _: WarenkorbEntwurf => Bestellfertigkeit.NichtBestellfertig
    }
}

object Warenkorb {
  // Smart constructor
  def make(artikel: Seq[Artikel],
                   kunde: Option[Kunde],
                   lieferadressenEntwurf: AttributEntwurf[Adresse, GrundFürUnzulässigeLieferadresse],
                   zahlungsartEntwurf: AttributEntwurf[Zahlungsart, GrundfürUnzulässigeZahlungsart],
                   grußkarte: Option[Grußkarte]): Warenkorb = {
    def fallback = WarenkorbEntwurf(artikel, kunde, lieferadressenEntwurf, zahlungsartEntwurf, grußkarte)
    kunde match {
      case Some(kunde) =>
        attributEntwurfApplicative.map2(lieferadressenEntwurf.mapGrund(GrundFürUnzulässig.Lieferadresse),
          zahlungsartEntwurf.mapGrund(GrundFürUnzulässig.Zahlungsart)) {
          (lieferadresse, zahlungsart) =>
            WarenkorbBestellfertig(artikel, kunde, lieferadresse, zahlungsart, grußkarte) } match {
          case AttributIstDa(warenkorb) => warenkorb
          case AttributNichtDa => fallback
          case AttributUnzulässig(warenkorb, grund) => fallback
        }
      case None => fallback
    }
  }
}

enum Bestellfertigkeit {
  case Bestellfertig
  case NichtBestellfertig
}

import Warenkorb._

def artikelInDenWarenkorb(warenkorb: Warenkorb, artikel: Artikel): Warenkorb =
  warenkorb match {
    case WarenkorbBestellfertig(artikelVorher, kunde, lieferadresse, zahlungsart, grußkarte) =>
      Warenkorb.make(artikelVorher :+ artikel, Some(kunde),
        überprüfeLieferadresse(lieferadresse, artikel),
        überprüfeZahlungsart(zahlungsart, artikel),
        grußkarte)
    case WarenkorbEntwurf(artikel, kunde, lieferadresse, zahlungsart, grußkarte) => ???
  }

enum GrundFürUnzulässig {
  case Lieferadresse(grund: GrundFürUnzulässigeLieferadresse)
  case Zahlungsart(grund: GrundfürUnzulässigeZahlungsart)
}


def überprüfeZahlungsart(zahlungsart: Zahlungsart, artikel: Artikel)
  : AttributEntwurf[Zahlungsart, GrundfürUnzulässigeZahlungsart] =
  (zahlungsart, artikel.kategorie) match {
    case (Zahlungsart.Paypal, ArtikelKategorie.Kosmetik) =>
      AttributUnzulässig(zahlungsart, Seq(GrundfürUnzulässigeZahlungsart.PaypalNichtBeiKosmetik))
    case _ => AttributIstDa(zahlungsart)
  }

def überprüfeLieferadresse(lieferadresse: Adresse, artikel: Artikel)
  : AttributEntwurf[Adresse, GrundFürUnzulässigeLieferadresse] = ???
