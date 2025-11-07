import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.time.measureTime

fun main() {
    val repeatCount = 10000

    GlobalScope.launch {
        repeat(5) {
            measureTime {
                repeat(repeatCount) {
                    awaitPromise()
                }
            }.let {
                println("AwaitPromise time x$repeatCount = $it, avg=${it / repeatCount}\n")
            }
        }
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
suspend fun awaitPromise() {
    suspendCoroutine { continuation ->
        Promise.resolve<JsAny?>(null).then(
            onFulfilled = {
                continuation.resumeWith(Result.success(Unit))
                null
            },
            onRejected = { null }
        )
    }
}