// Ein optionaler Wert (vom Typ: A) ist eins der folgenden:
// - nicht da
// - oder doch
sealed interface Optional<out A>
object NotThere : Optional<Nothing>
data class There<out A>(val value: A) : Optional<A>

sealed interface List<out A>
object Empty : List<Nothing>
data class Cons<out A>(val first: A, val rest: List<A>)