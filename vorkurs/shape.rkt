#lang deinprogramm/sdp/beginner

; Inspiration: Geometric Region Server

; Geometrische Form ("Shape", "Form"):
; - Kreis
; - Quadrat
; - eine Überlagerung zweiter geometrischer Formen

; Aufgabe:
; - Datenmodell
; - paar Beispiele
; - Funktion, die für einen Punkt ermittelt, ob er innerhalb oder außerhalb einer Form ist.

(define shape
  (signature (mixed square circle overlay)))

; Ein Punkt besteht aus:
; - X-Koordinate
; - Y-Koordinate
(define-record point
  make-point
  (point-x real)
  (point-y real))

; Ein Quadrat hat folgende Eigenschaften:
; - untere linke Ecke
; - Seitenlänge
(define-record square
  make-square
  square?
  (square-ll-corner point)
  (square-side-length real))

(define square1 (make-square (make-point 5 7) 3))

; Ein Kreis hat folgende Eigenschaften:
; - Mittelpunkt
; - Radius
(define-record circle
  make-circle
  circle?
  (circle-center point)
  (circle-radius real))

(define circle1 (make-circle (make-point 12 24) 3))


; Eine Überlagerung besteht aus:
; - Figur
; - noch 'ne Figur
(define-record overlay
  make-overlay
  overlay?
  (overlay-shape-1 shape) ; Selbstbezug
  (overlay-shape-2 shape))

(define overlay1 (make-overlay square1 circle1))

; Ist ein Punkt innerhalb eines Kreises?
(: in-circle? (point circle -> boolean))

(check-expect (in-circle? (make-point 13 24) circle1) #t)
(check-expect (in-circle? (make-point 20 30) circle1) #f)

(define in-circle?
  (lambda (point circle)
    (<= (distance point (circle-center circle))
        (circle-radius circle))))

; Abstand zwischen zwei Punkten berechnen
(: distance (point point -> real))

(define distance
  (lambda (point1 point2)
    (define dx (- (point-x point2) (point-x point1)))
    (define dy (- (point-y point2) (point-y point1)))
    (sqrt (+ (* dx dx) (* dy dy)))))

; Punkt in Quadrat?
(: in-square? (point square -> boolean))

(check-expect (in-square? (make-point 6 9) square1) #t)
(check-expect (in-square? (make-point 9 9) square1) #f)

(define in-square?
  (lambda (point square)
    (define px (point-x point))
    (define py (point-y point))
    (define ll (square-ll-corner square))
    (define sl (square-side-length square))
    (and (>= px (point-x ll))
         (>= py (point-y ll))
         (<= px (+ (point-x ll) sl))
         (<= py (+ (point-y ll) sl)))))

; Punkt in geometrischer Figur?
(: in-shape? (point shape -> boolean))

(check-expect (in-shape? (make-point 6 9) overlay1) #t)
(check-expect (in-shape? (make-point 20 30) overlay1) #f)


(define in-shape?
  (lambda (point shape)
    (cond
      ((square? shape) (in-square? point shape))
      ((circle? shape) (in-circle? point shape))
      ((overlay? shape) (or (in-shape? point (overlay-shape-1 shape))
                            (in-shape? point (overlay-shape-2 shape)))
                        ))))
             
