package org.example.project

import kotlinx.browser.window
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

class Tests {

    suspend fun awaitAnimationFrame() {
        suspendCoroutine { continuation ->
            window.requestAnimationFrame { continuation.resumeWith(Result.success(it)) }
        }
    }

    @Test
    fun measureAnimationFrame() = runTest {
        val repeatCount = 20
        repeat(5) {
            measureTime {
                repeat(repeatCount) {
                    awaitAnimationFrame()
                }
            }.let {
                println("AnimationFrame time x$repeatCount = $it, avg=${it / repeatCount}\n")
            }
        }
    }

    @OptIn(ExperimentalWasmJsInterop::class)
    suspend fun awaitSetTimeout(timeMs: Int) {
        suspendCoroutine { continuation ->
            window.setTimeout({
                continuation.resumeWith(Result.success(Unit))
                null
            }, timeMs)
        }
    }

    @Test
    fun measureSetTimeout() = runTest {
        val repeatCount = 20
        repeat(5) {
            measureTime {
                repeat(repeatCount) {
                    awaitSetTimeout(10)
                }
            }.let {
                println("SetTimeout time x$repeatCount = $it, avg=${it / repeatCount}\n")
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

    @Test
    fun measurePromise() = runTest {
        val repeatCount = 10000
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


    suspend fun immediateContinuation() {
        suspendCoroutine { continuation ->
            continuation.resumeWith(Result.success(Unit))
        }
    }

    @Test
    fun measureImmediateContinuation() = runTest {
        val repeatCount = 100000
        repeat(5) {
            measureTime {
                repeat(repeatCount) {
                    immediateContinuation()
                }
            }.let {
                println("ImmediateContinuation time x$repeatCount = $it, avg=${it / repeatCount}\n")
            }
        }
    }

    @Test
    fun measureJustALoop() = runTest {
        val repeatCount = 100_000_000
        repeat(5) {
            measureTime {
                repeat(repeatCount) {
                    // NoOp
                }
            }.let {
                println("Loop time x$repeatCount = $it, avg=${it / repeatCount}\n")
            }
        }
    }

    suspend fun realDelay(timeMs: Long) {
        withContext(Dispatchers.Default) {
            delay(timeMs)
        }
    }

    @Test
    fun measureDelay() = runTest {
        val repeatCount = 50
        repeat(3) {
            measureTime {
                repeat(repeatCount) {
                    realDelay(10)
                }
            }.let {
                println("Delay time x$repeatCount = $it, avg=${it / repeatCount}\n")
            }
        }
    }
}