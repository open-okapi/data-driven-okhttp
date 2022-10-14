package org.okapi.lib.okhttp.listener

import okhttp3.*
import org.okapi.lib.okhttp.sub.NetEventModel
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy


/**
 * author chen.kang
 */
class OkHttpEventListener(private val model: NetEventModel) : EventListener() {
    private var callStart: Long = 0

    // connection
    private var dnsStart: Long = 0
    private var connectStart: Long = 0
    private var secureConnectStart: Long = 0

    // request after connection
    private var requestStart: Long = 0
    private var responseStart: Long = 0

    private val max: Long = 30000

    override fun callStart(call: Call) {
        callStart = System.currentTimeMillis()
    }

    override fun dnsStart(call: Call, domainName: String) {
        dnsStart = System.currentTimeMillis()
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
        model.dnsDuration = System.currentTimeMillis() - dnsStart
        if (model.dnsDuration >= max) {
            model.dnsDuration = 0
        }
    }

    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
        connectStart = System.currentTimeMillis()
    }

    override fun secureConnectStart(call: Call) {
        secureConnectStart = System.currentTimeMillis()
    }

    override fun secureConnectEnd(call: Call, handshake: Handshake?) {
        model.secureDuration = System.currentTimeMillis() - secureConnectStart
        if (model.secureDuration >= max) {
            model.secureDuration = 0
        }
    }

    override fun connectEnd(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?) {
        model.connectDuration = System.currentTimeMillis() - connectStart
        if (model.connectDuration >= max) {
            model.connectDuration = 0
        }
        // connectionAcquired could be same times, be sure request start from there
        requestStart = System.currentTimeMillis()
        model.requestDuration = 0
    }

    override fun connectFailed(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?,
        ioe: IOException
    ) {
        model.connectDuration = System.currentTimeMillis() - connectStart
        if (model.connectDuration >= max) {
            model.connectDuration = 0
        }
    }

    override fun requestHeadersEnd(call: Call, request: Request) {
        model.requestDuration = System.currentTimeMillis() - requestStart
        if (model.requestDuration >= max) {
            model.requestDuration = 0
        }
    }

    // do not exec when there has not request body
    override fun requestBodyEnd(call: Call, byteCount: Long) {
        model.requestDuration = System.currentTimeMillis() - requestStart
        if (model.requestDuration >= max) {
            model.requestDuration = 0
        }
    }

    override fun responseHeadersStart(call: Call) {
        if (responseStart == 0L) {
            responseStart = System.currentTimeMillis()
        }
    }

    override fun responseHeadersEnd(call: Call, response: Response) {
        model.responseDuration = System.currentTimeMillis() - responseStart
        if (model.responseDuration >= max) {
            model.responseDuration = 0
        }
    }

    override fun responseBodyStart(call: Call) {
        if (responseStart == 0L) {
            responseStart = System.currentTimeMillis()
        }
    }

    override fun responseBodyEnd(call: Call, byteCount: Long) {
        model.responseDuration = System.currentTimeMillis() - responseStart
        if (model.responseDuration >= max) {
            model.responseDuration = 0
        }
        model.serveDuration = responseStart - (requestStart + model.requestDuration) + model.responseDuration
        if (model.serveDuration >= max) {
            model.serveDuration = 0
        }
    }

    override fun callEnd(call: Call) {
        model.fetchDuration = System.currentTimeMillis() - callStart
        if (model.fetchDuration >= max) {
            model.fetchDuration = 0
        }
    }

    override fun callFailed(call: Call, ioe: IOException) {
        model.fetchDuration = System.currentTimeMillis() - callStart
        if (model.fetchDuration >= max) {
            model.fetchDuration = 0
        }
    }
}