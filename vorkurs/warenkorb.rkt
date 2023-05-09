#lang deinprogramm/sdp
; Warenkörbe

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
(define-record warenkorb-bestellfertig
  make-warenkorb-bestellfertig
  warenkorb-bestellfertig?
  (warenkorb-bestellfertig-artikel (list-of artikel))
  (warenkorb-bestellfertig-kunde kunde)
  (warenkorb-bestellfertig-lieferadresse adresse)
  (warenkorb-bestellfertig-zahlungsart zahlungsart))

#|
- erstmal Position bauen
- dann Validator laufenlassen

- Yaron Minsky: "Make illegal states unrepresentable."
- User-Input -> Domänenobjekt

data class Position(
    @field:Min(1, message = "ANZAHL_ZERO") var anzahl: Int,
    @field:Valid var preis: Preis,
    @field:Valid var produkt: Produkt
)
|#
