// Warenkörbe

/*
; Lebenszyklus
; Lieferadresse
; Gutscheine
; Zahlart
; Validierung

; nicht jeden Artikel an jede Adresse
; nicht jede Zahlart

; - Artikel in den Warenkorb legen
; - in den Checkout gehen
; - Lieferart / Zahlart angeben
; - weiterhin Artikel in den Warenkorb legen
; - Artikel entfernen
; - bei Kosmetikartikeln wird die Zahlart Paypal ungültig
; - (nur gültige) Bestellung abschicken

; Ein Warenkorb ist eins der folgenden:
; - den Entwurfs-Warenkorb -ODER-
; - ein bestellfertigen Warenkorb

; Der Entwurfs-Warenkorb besteht aus:
; - den Artikeln
; - Kunde ODER NICHT
; - Lieferadresse ODER NICHT ODER eine unzulässige Lieferadresse
; - Zahlungsart ODER NICHT ODER eine unzulässige Zahlungsart

; Der bestellfertige Warenkorb besteht aus:
; - den Artikeln
; - Kunde
; - Lieferadresse
; - Zahlungsart
*/

enum class Artikel { KOSMETIK, MODE, MÖBEL, LEBENSMITTEL }
interface Kunde
interface Adresse
enum class Zahlungsart { PAYPAL, BITCOIN, SEPA }
interface Grußkarte

// Ein optionaler Wert (vom Typ: A) ist eins der folgenden:
// - nicht da
// - oder doch
sealed interface Optional<out A>
object NotThere : Optional<Nothing>
data class There<out A>(val value: A) : Optional<A>

/*
sealed interface LieferadressenEntwurf
data class EineLieferaddresse(val adresse: Adresse) : LieferadressenEntwurf
data class UnzulässigeLieferadresse(val adresse: Adresse,
                                    val grund: GrundFürUnzulässigeLieferaddresse) : LieferadressenEntwurf
object KeineLieferAdresse : LieferadressenEntwurf
*/

sealed interface AttributEntwurf<out T, out GRUND>
data class AttributIstDa<out T>(val wert: T) : AttributEntwurf<T, Nothing>
data class AttributUnzulässig<out T, out GRUND>(val wert: T, val grund: GRUND)
    : AttributEntwurf<T, GRUND>
object AttributNichtDa : AttributEntwurf<Nothing, Nothing>

enum class GrundFürUnzulässigeLieferaddresse {
    PACKSTATION, MIESEGEGEND
}

enum class GrundFürUnzulässigeZahlungsart {
    BITCOIN, SCHUFA, PAYPAL_NICHT_BEI_KOSMETIK
}

sealed interface Warenkorb

data class WarenkorbBestellfertig(
    val artikel: List<Artikel>,
    val kunde: Kunde,
    val adresse: Adresse,
    val zahlungsart: Zahlungsart,
    val grußkarte: Optional<Grußkarte>
) : Warenkorb

/* Ein Lieferadressen-Entwurf ist eins der folgenden
- eine Lieferadresse - ODER -
- eine unzulässige Lieferadresse, hat als Eigenschaft Grund
- keine Lieferadresse
 */

data class WarenkorbEntwurf(
    val artikel: List<Artikel>,
    val kunde: Optional<Kunde>,
    val lieferadresse: AttributEntwurf<Adresse, GrundFürUnzulässigeLieferaddresse>,
    val zahlungsart: AttributEntwurf<Zahlungsart, GrundFürUnzulässigeZahlungsart>
    val grußkarte: Optional<Grußkarte>
) : Warenkorb

fun artikelInDenWarenkorb(warenkorb: Warenkorb, artikel: Artikel): Warenkorb =
    when (warenkorb) { // Verzweigung
        is WarenkorbBestellfertig ->
            warenkorb.artikel +
        is WarenkorbEntwurf -> TODO()
    }

fun überprüfeZahlungsart(zahlungsart: Zahlungsart, artikel: Artikel)
  : AttributEntwurf<Zahlungsart, GrundFürUnzulässigeZahlungsart> =
    if (zahlungsart == Zahlungsart.PAYPAL && artikel == Artikel.KOSMETIK)
        AttributUnzulässig(zahlungsart, GrundFürUnzulässigeZahlungsart.PAYPAL_NICHT_BEI_KOSMETIK)
    else
        AttributIstDa(zahlungsart)

fun überprüfeLieferadresse(lieferaddresse: Adresse, artikel: Artikel)
    : AttributEntwurf<Adresse, GrundFürUnzulässigeLieferaddresse> = TODO()