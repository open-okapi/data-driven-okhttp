package org.okapi.lib.okhttp.request

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.okapi.lib.okhttp.enums.PairValueType
import org.okapi.lib.okhttp.sub.OkHttpMeta
import java.io.File

/**
 * author chen.kang
 */
object Assemble {

    // mix text and file
    private val FORM = "multipart/form-data".toMediaType()

    // normal form text name pairs
    private val FORM_NORMAL = "application/x-www-form-urlencoded".toMediaType()

    // raw json/text/xml/xxx
    private val RAW_JSON = "application/json".toMediaType()

    // file stream
    private val OCTET_STREAM = "application/octet-stream".toMediaType()

    private fun assembleHttpEntities(meta: OkHttpMeta): RequestBody? {
        val entities = meta.entities
        val valuePairs = meta.valuePairs
        var body: RequestBody? = null
        if (entities != null) {

            // name pairs form
            if (entities.type == FORM_NORMAL.toString()) {
                val bodyBuilder = FormBody.Builder()
                valuePairs.forEach { vp ->
                    vp.value?.let { v -> bodyBuilder.add(vp.key, v) }
                }
                body = bodyBuilder.build()
            }
            // form mixed with file or text
            if (entities.type == FORM.toString()) {
                val bodyBuilder = MultipartBody.Builder()
                valuePairs.forEach { vp ->
                    vp.value?.let { v ->
                        run {
                            if (vp.valueType == PairValueType.TEXT) {
                                bodyBuilder.addFormDataPart(vp.key, v)
                            }
                            if (vp.valueType == PairValueType.FILE) {
                                // 添加文件 body
                                val file = File(v)
                                val requestFileBody = file.asRequestBody(OCTET_STREAM)
                                bodyBuilder.addFormDataPart(vp.key, "file", requestFileBody)
                            }
                        }
                    }
                }
                bodyBuilder.setType(FORM)
                body = bodyBuilder.build()
            }
            // raw json
            if (entities.type == RAW_JSON.toString()) {
                body = entities.data.toRequestBody(RAW_JSON)
            }
            // default or not supports request type
            if (body == null) {
                body = entities.data.toRequestBody()
            }
        }
        return body
    }

    private fun assembleHttpUri(meta: OkHttpMeta): HttpUrl {
        val httpModel = meta.httpModel
        val params = meta.params
        val httpUrlBuilder = HttpUrl.Builder()
        // add base url info
        httpUrlBuilder.scheme(httpModel.httpSchema)
            .host(httpModel.httpHost)
            .port(httpModel.httpPort)
            .addPathSegments(httpModel.httpPath)
        // add query info
        params.forEach {
            httpUrlBuilder.addQueryParameter(it.key, it.value)
        }
        // add auth info
        httpModel.baseAuth?.let {
            httpUrlBuilder.username(it.first)
                .password(it.second)
        }
        return httpUrlBuilder.build()
    }

    fun assembleHttpRequest(builder: Request.Builder, meta: OkHttpMeta): Request {
        // get data group
        val httpModel = meta.httpModel
        val headers = meta.headers
        // parse data to request model
        // body
        val body = assembleHttpEntities(meta)
        // url
        val httpUrl = assembleHttpUri(meta)
        builder.url(httpUrl)
        // method
        builder.method(httpModel.httpMethod, body)
        // header
        headers.forEach { header ->
            run {
                header.value?.let {
                    builder.addHeader(header.key, it)
                }
            }
        }
        return builder.build()
    }

}