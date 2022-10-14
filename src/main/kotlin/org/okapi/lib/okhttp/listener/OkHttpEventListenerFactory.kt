package org.okapi.lib.okhttp.listener

import okhttp3.Call
import okhttp3.EventListener
import org.okapi.lib.okhttp.sub.NetEventModel

/**
 * author chen.kang
 */
class OkHttpEventListenerFactory : EventListener.Factory {
    override fun create(call: Call): EventListener {
        val tag = call.request().tag(NetEventModel::class.java)
        return tag?.let { OkHttpEventListener(it) } ?: EventListener.NONE
    }
}