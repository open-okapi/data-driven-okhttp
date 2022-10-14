package org.okapi.lib.okhttp

import okhttp3.Request
import org.okapi.lib.okhttp.config.ClientFactory
import org.okapi.lib.okhttp.request.Assemble
import org.okapi.lib.okhttp.response.BodyFork
import org.okapi.lib.okhttp.sub.NetEventModel
import org.okapi.lib.okhttp.sub.OkHttpMeta

/**
 * author chen.kang
 */
object Client {
    // 请求执行流程
    private fun exec(meta: OkHttpMeta): OkHttpMeta {
        // 获取请求实例
        val client = ClientFactory.getClient()
        // 请求数据模型
        val builder: Request.Builder = Request.Builder()
        val netEventModel = NetEventModel()
        builder.tag(NetEventModel::class.java, netEventModel)
        // 组装请求内容
        val req = Assemble.assembleHttpRequest(builder, meta)
        // 执行请求
        val res = client.newCall(req).execute()
        // 处理响应 body
        BodyFork.process(res, meta)
        meta.netEventModel = netEventModel
        // 返回执行结果和过程分析数据
        return meta
    }

    // 执行请求入口方法
    fun request(meta: OkHttpMeta): OkHttpMeta {
        try {
            exec(meta)
        } catch (t: Throwable) {
            meta.recordStem("request error：${t.message}")
        }
        return meta
    }
}