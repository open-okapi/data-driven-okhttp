package org.okapi.lib.okhttp.sub

/**
 * author chen.kang
 */
class NetEventModel {
    var fetchDuration // 请求发出到拿到数据，不包括本地排队时间
            : Long = 0
    var dnsDuration  //dns
            : Long = 0
    var connectDuration // 创建socket通道时间
            : Long = 0
    var secureDuration // ssl握手时间，connect_duration包含secure_duration
            : Long = 0
    var requestDuration // writeBytes的时间
            : Long = 0
    var responseDuration // readBytes的时间
            : Long = 0
    var serveDuration // 相当于responseStartDate - requestEndDate
            : Long = 0

    override fun toString(): String {
        return "NetEventModel(fetchDuration=$fetchDuration, dnsDuration=$dnsDuration, connectDuration=$connectDuration, secureDuration=$secureDuration, requestDuration=$requestDuration, responseDuration=$responseDuration, serveDuration=$serveDuration)"
    }

}