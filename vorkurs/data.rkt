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

(define cute?
  (lambda (pet)
    ; Verzweigung / passend zur Fallunterscheidung
    (cond ; 1 Zweig pro Fall
      ; jeder Zweig: (<Bedingung> <Antwort>)
      ((string=? pet "turtle") #t)
      ((string=? pet "cat") #t)
      ((string=? pet "snake") #f))))

