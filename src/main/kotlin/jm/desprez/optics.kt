package jm.desprez

import org.http4k.core.HttpMessage
import org.http4k.lens.BiDiBodyLens

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

    fun <B> chain(lens: Lens<A, B>): Lens<S, B> = Lens(
            { s -> lens(getter(s)) },
            { newValue -> { s -> setter(lens.setter(newValue)(getter(s)))(s) } }
    )

    fun <T> from(iso: Iso<T, S>): Lens<T, A> = Lens(
            { getter(iso(it)) },
            { a -> { t -> iso.swap(setter(a)(iso(t))) } }
    )
}

fun <T, V> BiDiBodyLens<T>.chain(lens: Lens<T, V>): Lens<HttpMessage, V> = Lens(
        { httpMessage -> lens(extract(httpMessage)) },
        { newValue -> { httpMessage -> this.of<HttpMessage>(lens.setter(newValue)(extract(httpMessage)))(httpMessage) } }
)

fun <T> BiDiBodyLens<T>.with(map: T.() -> T): BiDiBodyLens<T> = BiDiBodyLens(metas, contentType,
                                                                             { httpMessage -> extract(httpMessage).map() },
                                                                             { t, httpMessage ->
                                                                                 inject(t.map(),
                                                                                        httpMessage)
                                                                             }
)