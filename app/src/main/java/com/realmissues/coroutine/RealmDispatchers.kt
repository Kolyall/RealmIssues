package com.realmissues.coroutine

import android.os.Handler
import android.os.HandlerThread
import kotlinx.coroutines.CoroutineDispatcher
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

object RealmDispatchers {

    // Create a HandlerThread and start it
    private val handlerThread = HandlerThread("Realm Handler Thread").apply { start() }

    // Create a CoroutineDispatcher that uses the HandlerThread's Looper
    val DB = handlerThread.toCoroutineDispatcher()

}

private fun HandlerThread.toCoroutineDispatcher(): CoroutineDispatcher {
    return HandlerThreadDispatcher(this)
}

class HandlerThreadDispatcher(private val handlerThread: HandlerThread) : CoroutineDispatcher(),
    Closeable {
    private val handler = Handler(handlerThread.looper)

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        handler.post(block)
    }

    override fun close() {
        handlerThread.quitSafely()
    }
}
