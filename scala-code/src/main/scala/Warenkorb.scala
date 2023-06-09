import AttributEntwurf.AttributUnzulässig

enum Artikel {
  case Kosmetik
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
  case AttributUnzulässig(wert: T, grund: GRUND)
  case AttributNichtDa
}

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
}

import Warenkorb._

def artikelInDenWarenkorb(warenkorb: Warenkorb, artikel: Artikel): Warenkorb =
  warenkorb match {
    case WarenkorbBestellfertig(artikelVorher, kunde, lieferadresse, zahlungsart, grußkarte) =>
      WarenkorbEntwurf(artikelVorher :+ artikel, Some(kunde),
        überprüfeLieferadresse(lieferadresse, artikel),
        überprüfeZahlungsart(zahlungsart, artikel),
        grußkarte)
    case WarenkorbEntwurf(artikel, kunde, lieferadresse, zahlungsart, grußkarte) => ???
  }

def warenkorb(artikel: Seq[Artikel],
  kunde: Option[Kunde],
  lieferadresse: AttributEntwurf[Adresse, GrundFürUnzulässigeLieferadresse],
  zahlungsart: AttributEntwurf[Zahlungsart, GrundfürUnzulässigeZahlungsart],
  grußkarte: Option[Grußkarte]) =
  kunde match {
    case Some(kunde) =>
      lieferadresse match {
        case AttributIstDa(lieferadresse) =>
          zahlungsart match {
            case AttributIstDa(zahlungsart) =>
              WarenkorbBestellfertig(artikel, kunde,
                lieferadresse, zahlungsart, grußkarte)
            case AttributUnzulässig(zahlungsart, grund) => ???
            case AttributNichtDa => ???
          }
        case AttributUnzulässig(lieferadresse, grund) => ???
        case AttributNichtDa => ???
      }
    case None => ???
  }

def überprüfeZahlungsart(zahlungsart: Zahlungsart, artikel: Artikel)
  : AttributEntwurf[Zahlungsart, GrundfürUnzulässigeZahlungsart] =
  (zahlungsart, artikel) match {
    case (Zahlungsart.Paypal, Artikel.Kosmetik) =>
      AttributUnzulässig(zahlungsart, GrundfürUnzulässigeZahlungsart.PaypalNichtBeiKosmetik)
    case _ => AttributIstDa(zahlungsart)
  }

def überprüfeLieferadresse(lieferadresse: Adresse, artikel: Artikel)
  : AttributEntwurf[Adresse, GrundFürUnzulässigeLieferadresse] = ???