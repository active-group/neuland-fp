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


; Gürteltier hat folgende Eigenschaften:
; - lebendig oder tot -UND-
; - Gewicht

; Zustand des Gürteltiers zu einem bestimmten Zeitpunkt
(define-record dillo
  make-dillo
  (dillo-liveness liveness)
  (dillo-weight number))

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

  

  