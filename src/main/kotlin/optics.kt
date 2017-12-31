/**
 * An isomorphism between A and B
 */
class Iso<A, B>(val map: (A) -> B, val reverse: (B) -> A) {
    val swap: Iso<B, A> by lazy { Iso(reverse, map) }

    operator fun invoke(a: A): B = map(a)

    fun <C> compose(iso: Iso<B, C>): Iso<A, C> = Iso({ a -> iso(map(a)) }, { c -> reverse(iso.swap(c)) })
}

class Lens<S, A>(val getter: (S) -> A, val setter: (A) -> (S) -> S) {
    operator fun invoke(s: S): A = getter(s)

    fun mapGetter(op: A.() -> A): (S) -> A = { s -> op(getter(s)) }
    fun map(op: A.() -> A): (S) -> S = { s -> setter(op(getter(s)))(s) }

    fun <T> lift(iso: Iso<T, S>): Lens<T, A> = Lens(
            { getter(iso(it)) },
            { a -> { t -> iso.swap(setter(a)(iso(t))) } }
    )
}
