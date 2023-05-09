// Ein optionaler Wert (vom Typ: A) ist eins der folgenden:
// - nicht da
// - oder doch
sealed interface Optional<out A>
object NotThere : Optional<Nothing>
data class There<out A>(val value: A) : Optional<A>

sealed interface List<out A>
object Empty : List<Nothing>
data class Cons<out A>(val first: A, val rest: List<A>) : List<A>

fun <A, B> listMap(f: (A) -> B, list: List<A>): List<B> =
    when (list) {
        is Empty -> Empty
        is Cons ->
            Cons(f(list.first),
                 listMap(f, list.rest))
    }

fun <A, B> optionalMap(f: (A) -> B, optional: Optional<A>): Optional<B> =
    when (optional) {
        is NotThere -> NotThere
        is There -> There(f(optional.value))
    }

fun <A> listIndex(element: A, list: List<A>): Optional<Int> =
    when (list) {
        is Empty -> NotThere
        is Cons ->
            if (list.first == element)
                There(0)
            else {
                val restIndex = listIndex(element, list.rest)
                optionalMap({ (index) -> index + 1 }, restIndex)
/*                when (restIndex) {
                    is NotThere -> NotThere
                    is There -> There(restIndex.value + 1)
                }
                */
 */
            }
    }
