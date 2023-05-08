#lang deinprogramm/sdp/beginner
(require deinprogramm/sdp/image)

(define x
  (+ 12
     (* 23
        45)))

(define y (+ x 15))

(define circle1 (circle 50 "solid" "gold"))
(define square1 (square 100 "outline" "blue"))
(define star1 (star 50 "solid" "green"))
(define overlay1 (overlay star1 circle1))

(above
 (beside star1 circle1)
 (beside circle1 star1))

(above
 (beside square1 star1)
 (beside star1 square1))

; zwei ähnliche Codestellen, inhaltlich verwandt
; => Abstraktion
; - (ein letztes Mal) kopieren
; - Unterschiede durch (abstrakte) Namen ersetzen
; - lambda
(define tile
  (lambda (image1 image2)
    (above
     (beside image1 image2)
     (beside image2 image1))))