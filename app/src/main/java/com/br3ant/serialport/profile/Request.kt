package com.br3ant.serialport.profile

import com.br3ant.utils.hexToBytes
import com.br3ant.utils.toBytes
import com.br3ant.utils.toHexString
import kotlin.experimental.xor

open class Request(val cmd: String, val data: String) {

    fun command(): String {
        val len = (data.length / 2 + 1).toLong().toBytes().toHexString()
        val command = "$len$cmd$data"
        val bcc = command.hexToBytes().reduce { acc, byte -> acc.xor(byte) }.toLong().toBytes()
            .toHexString()
        return "0200${command + bcc}"
    }
}

class Device(data: String) : Request("2F", data)
object FindAllCard : Request("31", "52")
object FindNotIDLE : Request("31", "26")
object Anticoll : Request("32", "93")
class SelectCard(data: String) : Request("33", "93$data")
class LoadKey(key: String) : Request("35", key)
class Authentication(blockCode: Int, card: String) :
    Request("37", "60${blockCode.toLong().toBytes().toHexString()}$card")

class Read(blockCode: Int, areaCode: Int) : Request(
    "38", "${blockCode.toLong().toBytes().toHexString()}${
        areaCode.toLong().toBytes().toHexString()
    }"
)

