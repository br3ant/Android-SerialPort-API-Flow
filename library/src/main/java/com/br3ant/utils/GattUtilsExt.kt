package com.br3ant.utils

fun ByteArray.toHexString() = GattUtils.bytesToHexString(this)

fun String.hexToBytes() = GattUtils.hexStringToBytes(this)

fun Long.toBytes(bits: Int = 1) = GattUtils.numberToBytes(this, bits)