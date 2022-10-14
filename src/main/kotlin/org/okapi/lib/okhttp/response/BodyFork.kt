package org.okapi.lib.okhttp.response

import okhttp3.Response
import org.apache.commons.io.IOUtils
import org.okapi.lib.okhttp.sub.OkHttpMeta
import java.nio.charset.StandardCharsets

/**
 * author chen.kang
 */
object BodyFork {
    fun process(res: Response, meta: OkHttpMeta) {
        // status code
        meta.status = res.code
        // body stream
        res.body?.let {
            val inputStream = it.byteStream()
            val realResponse = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name())
            // response content
            meta.realResponse = realResponse
            meta.contentLength = it.contentLength()
            // response content type and charset
            it.contentType()?.let { mediaType ->
                {
                    meta.contentType = "${mediaType.type}/${mediaType.subtype}"
                    mediaType.charset()?.let { charset -> meta.contentCharset = charset.name() }
                }
            }
        }
        // close response
        res.close()
    }
}