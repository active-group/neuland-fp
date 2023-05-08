#lang deinprogramm/sdp/beginner

; Datenmodellierung

; Datendefinition

; Ein Haustier ist eins der folgenden:
; - Schildkröte -ODER-
; - Katze -ODER-
; - Schlange
; Fallunterscheidung
; speziell: Aufzählung
(define pet
  (signature (enum "turtle" "cat" "snake")))

; Ist ein Haustier niedlich?
(: cute? (pet -> boolean))

(check-expect (cute? "turtle") #t)
(check-expect (cute? "cat") #t)
(check-expect (cute? "snake") #f)

; lexikalische / statische Bindung
; unterscheiden zwischen Vorkommen und Bindungen
; int x; <- Bindung
; ... x ... <- Vorkommen

; vom Korkommen zur Bindung:
; Wir suchen von innen nach außen zunächst nach lambda
; wenn nicht: define
; wenn nicht: importiert 


; Gerüst
#;(define cute?
  (lambda (pet)
    ...))

; Schablone
#;(define cute?
  (lambda (pet)
    ; Verzweigung / passend zur Fallunterscheidung
    (cond ; 1 Zweig pro Fall
      ; jeder Zweig: (<Bedingung> <Antwort>)
      ((string=? pet "turtle") ...)
      ((string=? pet "cat") ...)
      ((string=? pet "snake") ...))))

#;(define cute?
  (lambda (pet)
    ; Verzweigung / passend zur Fallunterscheidung
    (cond ; 1 Zweig pro Fall
      ; jeder Zweig: (<Bedingung> <Antwort>)
      ((string=? pet "turtle") #t)
      ((string=? pet "cat") #t)
      ((string=? pet "snake") #f))))

(define cute?
  (lambda (pet)
    (match pet
      ("turtle" #t)
      ("cat" #t)
      ("snake" #f))))

;(cute? "dog")

(define hour (signature (integer-from-to 0 23)))
(define minute (signature (integer-from-to 0 59)))

; Zeit hat folgende Eigenschaften / besteht aus:
; - Stunde -UND-
; - Minute
; zusammengesetzte Daten
(define-record time ; Signatur
  make-time ; Konstruktor
  (time-hour hour) ; Selektoren
  (time-minute minute))

; natural: Signatur für natürliche Zahlen 0,1,2,3,...

(: make-time (hour minute -> time))
(: time-hour (time -> hour))
(: time-minute (time -> minute))

; 11 Uhr 13 Minuten
(define time1 (make-time 11 13))
; 14:21
(define time2 (make-time 14 21))

; Wieviele Minuten seit Mitternacht?
(: minutes-since-midnight (time -> natural))

(check-expect (minutes-since-midnight time1)
              673)
(check-expect (minutes-since-midnight time2)
              861)

; Schablone
#;(define minutes-since-midnight
  (lambda (time)
    ... (time-hour time) ... (time-minute time) ...))

; Schablone für zusammengesetzte Daten als Ausgabe:
; Aufruf des Konstruktors

(define minutes-since-midnight
  (lambda (time)
    (+ (* 60 (time-hour time))
       (time-minute time))))

; Tier auf dem texanischen Highway ist eins der folgenden:
; - Gürteltier -ODER-
; - Papagei
; Fallunterscheidung mit separaten Datendefinitionen
; gemischte Daten
(define animal
  (signature (mixed dillo parrot)))


; Gürteltier hat folgende Eigenschaften:
; - lebendig oder tot -UND-
; - Gewicht

; Zustand des Gürteltiers zu einem bestimmten Zeitpunkt
(define-record dillo
  make-dillo
  dillo? ; Prädikat
  (dillo-liveness liveness)
  (dillo-weight number))

(: dillo? (any -> boolean))

; Die "Lebendigkeit" ist eins der folgenden:
; - lebendig -ODER-
; - tot
(define liveness
  (signature (enum "alive" "dead")))

; lebendiges Gürteltier, 10kg
(define dillo1 (make-dillo "alive" 10))
; totes Gürteltier, 8kg
(define dillo2 (make-dillo "dead" 8))

; Gürteltier überfahren
#|
class Dillo {
  Liveness liveness;
  double weight;

  void runOver() {
     this.liveness = Liveness.DEAD;
  }
}
|#

(: run-over-dillo (dillo -> dillo))

(check-expect (run-over-dillo dillo1)
              (make-dillo "dead" 10))
(check-expect (run-over-dillo dillo2)
              dillo2)

#;(define run-over-dillo
  (lambda (dillo)
    (make-dillo "dead" (dillo-weight dillo))))

(define run-over-dillo
  (lambda (dillo)
    (cond
      ((string=? "alive" (dillo-liveness dillo))
       (make-dillo "dead" (dillo-weight dillo)))
      ((string=? "dead" (dillo-liveness dillo)) dillo))))


; Gürteltier füttern (Übungsaufgabe)

; Ein Papagei hat folgende Eigenschaften:
; - Satz
; - Gewicht
(define-record parrot
  make-parrot
  parrot?
  (parrot-sentence string)
  (parrot-weight number))

; Grußpapagei
(define parrot1 (make-parrot "Wilkommen!" 1))
; Abschiedspapagei
(define parrot2 (make-parrot "Tschüss!" 2))

; Papagei überfahren
(: run-over-parrot (parrot -> parrot))

(check-expect (run-over-parrot parrot1)
              (make-parrot "" 1))
(check-expect (run-over-parrot parrot2)
              (make-parrot "" 2))

(define run-over-parrot
  (lambda (parrot)
    (make-parrot "" (parrot-weight parrot))))

  
; Tier überfahren
(: run-over-animal (animal -> animal))

(check-expect (run-over-animal dillo1)
              (run-over-dillo dillo1))
(check-expect (run-over-animal parrot1)
              (run-over-parrot parrot1))

; Schablone
#;(define run-over-animal
  (lambda (animal)
    (cond
     ((dillo? animal) ...)
     ((parrot? animal) ...))))

(define run-over-animal
  (lambda (animal)
    (cond
     ((dillo? animal) (run-over-dillo animal))
     ((parrot? animal) (run-over-parrot animal))
     )))

#|
interface Animal {
   Animal runOver();
}

class Dillo implements Animal {
  @Override Animal runOver() { ...}
}

class Parrot implements Animal {
  @Override Animal runOver() { ... }
}

class Rattlesnake implements Animal {
  @Override Animal runOver() { ... }
}


|#

#|
Open/Closed:

neuer Fall:
... Klapperschlangen ...
- FP x, OOP yay

neue Operation
... füttern ...
- FP yay, OOP x 

|#

; Ein Duschprodukt ist eins der folgenden:
; - Shampoo, zählt Haartyp ("normal", "fettig", "Schuppen", "fein")
; - Seife, da gibt's pH-Wert
; - Duschgel, bestehend aus 50% Shampoo, 50% Seife
; - Mixtur bestehend aus zwei Duschprodukten
;                             ^^^^^^^^^^^^ Selbstbezug

; Wie hoch ist der Seifenanteil eines Duschprodukts?

(define-record shampoo
  make-shampoo
  shampoo?
  (shampoo-hair-type hair-type))

(define hair-type
  (signature (enum "normal" "oily" "dandruff")))

(define shampoo1 (make-shampoo "oily"))

(define-record soap
  make-soap
  soap?
  (soap-pH number))

(define soap1 (make-soap 7))

(define-record shower-gel
  make-shower-gel
  shower-gel?
  (shower-gel-shampoo shampoo)
  (shower-gel-soap soap))

; Eine Mixtur besteht aus:
; - nem Duschprodukt
; - noch nem Duschprodukt
(define-record mixture
  make-mixture
  mixture?
  (mixture-product1 shower-product)
  (mixture-product2 shower-product))

(define shower-product
  (signature (mixed shampoo soap shower-gel mixture)))

(define mixture1
  (make-mixture (make-shower-gel (make-shampoo "oily") (make-soap 5))
                (make-soap 7)))
(define mixture2
  (make-mixture mixture1 (make-shampoo "dandruff")))

; Wie hoch ist der Seifenanteil eines Duschprodukts?
(: shower-product-soap-proportion (shower-product -> number))

(check-expect (shower-product-soap-proportion soap1)
              1)
(check-expect (shower-product-soap-proportion shampoo1)
              0)
(check-expect (shower-product-soap-proportion (make-shower-gel shampoo1 soap1))
              0.5)
(check-expect (shower-product-soap-proportion mixture1)
              0.75)

(define shower-product-soap-proportion
  (lambda (shower-product)
    (cond
      ((soap? shower-product) 1)
      ((shampoo? shower-product) 0)
      ((shower-gel? shower-product) 0.5)
      ((mixture? shower-product)
       (/
        (+ (shower-product-soap-proportion (mixture-product1 shower-product))
           (shower-product-soap-proportion (mixture-product2 shower-product)))
        2)))))

; Datenanalyse:
; - Fallunterscheidung / gemischte Daten
; - zusammengesetzte Daten
; - Selbstbezüge

; Eine Liste ist eins der folgenden:
; - die leere Liste
; - eine Cons-Liste, bestehend aus erstem Element und Rest-Liste
;                                                          ^^^^^
(define list-of-numbers
  (signature (mixed empty-list cons-list)))

(define-singleton empty-list ; Signatur
  empty ; Singleton
  empty?) ; Prädikat
#;(define-record empty-list
  make-empty
  empty?)
#;(define empty (make-empty))

; Eine Cons-Liste besteht:
; - erstes Element
; - Rest-Liste
(define-record cons-list
  cons
  cons?
  (first number)
  (rest list-of-numbers))

; 1elementige Liste: 5
(define list1 (cons 5 empty))
; 2elementige Liste: 5 2
(define list2 (cons 5 (cons 2 empty)))
; 3elementige Liste: 8 5 2
(define list3 (cons 8 (cons 5 (cons 2 empty))))
; 4elementige Liste: 3 8 5 2
(define list4 (cons 3 list3))

; Summe aller Listenelemente berechnen
(: list-sum (list-of-numbers -> number))

(check-expect (list-sum list4)
              18)

; Schablone
#;(define list-sum
  (lambda (list)
    (cond
      ((empty? list) ...)
      ((cons? list)
       ...
       (first list)
       (list-sum (rest list))
       ...
       ...))))

(define list-sum
  (lambda (list)
    (cond
      ((empty? list) 0) ; "neutrales Element von +"
      ((cons? list)
       (+
        (first list)
        (list-sum (rest list)))))))
    
; Produkt aller Listenelemente
(: list-product (list-of-numbers -> number))

(check-expect (list-product list4)
              240)

(define list-product
  (lambda (list)
    (cond
      ((empty? list) 1) ; neutrales Element von *
      ((cons? list)
       (*
        (first list)
        (list-product (rest list)))))))

; (if <Bedingung> <then-Fall> <else-Fall>)

#;(define list-product
  (lambda (list)
    (if (empty? list)
        1
        (*
         (first list)
         (list-product (rest list))))))

#;(define list-product
  (lambda (list)
    (match list
      (empty 1)
      ((cons f r)
       (* f (list-product r))))))

; Aus einer Liste die ungeraden Elemente extrahieren
(: extract-odds (list-of-numbers -> list-of-numbers))

(check-expect (extract-odds list4)
              (cons 3 (cons 5 empty)))

(define extract-odds
  (lambda (list)
    (cond
      ((empty? list) empty)
      ((cons? list)
       (if (odd? (first list))
           (cons (first list) (extract-odds (rest list)))
           (extract-odds (rest list)))))))

; Abstraktion:
; - kopieren (letztes Mal)
; - umbenennen (an rekursive Aufrufe denken)
; - Unterschiede durch Namen ersetzen
; - lambda (an rekursive Aufrufe denken)

; eingebaut i.d.R. als filter
; Higher-Order-Funktion / Funktion höherer Funktion
(: extract ((number -> boolean) list-of-numbers -> list-of-numbers))

(check-expect (extract odd? list4)
              (cons 3 (cons 5 empty)))

(define extract
  (lambda (p? list)
    (cond
      ((empty? list) empty)
      ((cons? list)
       (if (p? (first list))
           (cons (first list) (extract p? (rest list)))
           (extract p? (rest list)))))))

(define highway (cons dillo1 (cons dillo2 (cons parrot1 (cons parrot2 empty)))))
