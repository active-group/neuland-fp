/*
T[A]

Klassen C und D, C <= D (C Unterklasse/Subtyp von D)

T[C] <= T[D] covariant
oder
T[D] <= T[C] contravariant

A => B
A => B'

B <= B' => (A => B) <= (A => B')

A' => B
A <= A'
(A => B) >= (A' => B)

 */

class Foo[-A] {
  def f(a: A): Int = ???
}