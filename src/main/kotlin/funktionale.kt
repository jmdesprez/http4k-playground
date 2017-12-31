import org.funktionale.tries.Try
import org.funktionale.tries.Try.*
import org.http4k.lens.LensExtractor
import org.http4k.lens.LensFailure

fun <IN, OUT> LensExtractor<IN, OUT>.toResult(): LensExtractor<IN, Try<OUT>> = object : LensExtractor<IN, Try<OUT>> {
    override fun invoke(target: IN): Try<OUT> = try {
        Success(this@toResult.invoke(target))
    } catch (e: LensFailure) {
        Failure(e)
    }
}
