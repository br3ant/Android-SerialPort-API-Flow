package com.br3ant.serialport.profile

import com.br3ant.utils.toHexString

open class Response(val status: Int, val data: String) {

    fun isSuccess() = status == 0
}

object Error : Response(1, "")

fun ByteArray.parse(): Response = kotlin.runCatching {
    val buf = this.drop(2)
    val len = buf.first().toInt()
    val payload = buf.subList(1, len)
    val status = payload.first().toInt()
    val data = payload.drop(1).toByteArray().toHexString()
    Response(status, data)
}.onFailure { it.printStackTrace() }.getOrElse { Error }

