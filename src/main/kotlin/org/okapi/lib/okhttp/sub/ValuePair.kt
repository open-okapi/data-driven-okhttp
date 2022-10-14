package org.okapi.lib.okhttp.sub

import org.okapi.lib.okhttp.enums.PairValueType


class ValuePair {
    lateinit var key: String
    var value: String? = null
    var valueType: String = PairValueType.TEXT
    var encryption: String? = null
    var secret: String? = null
    var doc: String? = null
}