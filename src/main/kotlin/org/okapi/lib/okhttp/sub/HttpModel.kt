package org.okapi.lib.okhttp.sub

class HttpModel {
    var httpSchema: String = "http"
    lateinit var httpMethod: String
    lateinit var httpHost: String
    var httpPort: Int = 80
    var httpPath: String = "/"

    // auth
    var baseAuth: Pair<String, String>? = null
}