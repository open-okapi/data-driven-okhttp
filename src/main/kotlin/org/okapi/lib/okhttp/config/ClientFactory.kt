package org.okapi.lib.okhttp.config

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.okapi.lib.okhttp.listener.OkHttpEventListenerFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


/**
 * author chen.kang
 */
object ClientFactory {

    private const val MAX_TIME_OUT: Long = 30
    private lateinit var client: OkHttpClient
    private lateinit var autoTrustManager: AutoTrustManager

    fun getClient(): OkHttpClient {
        return if (this::client.isInitialized) {
            client
        } else {
            client = initClient()
            client
        }
    }

    private fun initClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder
            // auto trust
            .sslSocketFactory(createSSLSocketFactory(), autoTrustManager)
            .connectTimeout(MAX_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(MAX_TIME_OUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            // define connection pool
            .connectionPool(ConnectionPool(100, 1, TimeUnit.MILLISECONDS))
            .writeTimeout(MAX_TIME_OUT, TimeUnit.SECONDS)
            // add event create process monitor
            .eventListenerFactory(OkHttpEventListenerFactory())
            // host trust config
            .hostnameVerifier(TrustAllHostnameVerifier())
        return builder.build()
    }

    private fun createSSLSocketFactory(): SSLSocketFactory {
        autoTrustManager = AutoTrustManager()
        val sc = SSLContext.getInstance("TLS")
        sc.init(null, arrayOf(autoTrustManager), SecureRandom())
        return sc.socketFactory
    }

    private class AutoTrustManager : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }

    // implement HostnameVerifier
    private class TrustAllHostnameVerifier : HostnameVerifier {
        override fun verify(hostname: String, session: SSLSession): Boolean {
            return true
        }
    }
}