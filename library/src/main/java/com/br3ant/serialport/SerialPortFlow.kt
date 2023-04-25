package com.br3ant.serialport

import android.serialport.SerialPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException


class SerialPortFlow(
    val path: String = "/system/bin/su",
    val baudrate: Int = 9600,
    val parity: Int = 0,
    val dataBits: Int = 8,
    val stopBits: Int = 1,
    val flag: Int = 0
) {

    private val buffer = ByteArray(64)

    // 可选配置数据位、校验位、停止位 - 7E2(7数据位、偶校验、2停止位)
    // or with builder (with optional configurations) - 7E2 (7 data bits, even parity, 2 stop bits)

    private val serialPort: SerialPort = SerialPort
        .newBuilder(path, baudrate) // 校验位；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
        // Check bit; 0: no check bit (NONE, default); 1: odd check bit (ODD); 2: even check bit (EVEN)
        .parity(parity)
        // 数据位,默认8；可选值为5~8
        // Data bit, default 8; optional value is 5~8
        .dataBits(dataBits)
        // 停止位，默认1；1:1位停止位；2:2位停止位
        // Stop bit, default 1; 1:1 stop bit; 2: 2 stop bit
        .stopBits(stopBits)
        .flags(0)
        .build()


    suspend fun write(bytes: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        try {
            serialPort.outputStream.write(bytes)
            delay(100)

            val size = serialPort.inputStream.read(buffer)

            buffer.copyOf(size)

        } catch (e: IOException) {
            e.printStackTrace()
            ByteArray(0)
        }
    }

    fun close() {
        serialPort.tryClose()
    }

    override fun toString(): String {
        return "SerialPort(path='$path', baudrate=$baudrate, parity=$parity, dataBits=$dataBits, stopBits=$stopBits, flag=$flag)"
    }
}

