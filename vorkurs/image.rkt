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
