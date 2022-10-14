package org.okapi.lib.okhttp.sub

/**
 * author chen.kang
 */
class OkHttpMeta {
    // rel 关联唯一 id
    lateinit var uuid: String

    // -------- in --------
    // 基本模型
    lateinit var httpModel: HttpModel

    // query 参数
    var params: MutableList<UrlParam> = mutableListOf()

    // http header 内容
    var headers: MutableList<Header> = mutableListOf()

    // 请求实体
    var entities: Entities? = null

    // 请求键值对
    var valuePairs: List<ValuePair> = mutableListOf()


    // -------- out --------
    // 步骤日志
    val logs: MutableList<String> = mutableListOf()

    // 过程监控
    lateinit var netEventModel: NetEventModel

    // 执行状态
    var status: Int = -1

    // 响应实体序列化字符串
    var realResponse: String = ""

    // 响应类型和编码
    var contentType: String = "application/default"
    var contentCharset: String = "utf-8"

    // 响应长度
    var contentLength: Long = 0


    fun recordStem(message: String) {
        this.logs.add(message)
    }
}