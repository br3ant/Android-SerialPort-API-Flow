package com.br3ant.serialport

import android.serialport.SerialPort
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException


class SerialPortFlow(
    path: String = "/system/bin/su",
    baudrate: Int = 9600,
    parity: Int = 0,
    dataBits: Int = 8,
    stopBits: Int = 8
) {

    private val serialPort: SerialPort

    init {
        // 可选配置数据位、校验位、停止位 - 7E2(7数据位、偶校验、2停止位)
        // or with builder (with optional configurations) - 7E2 (7 data bits, even parity, 2 stop bits)
        serialPort = SerialPort
            .newBuilder(path, baudrate) // 校验位；0:无校验位(NONE，默认)；1:奇校验位(ODD);2:偶校验位(EVEN)
            // Check bit; 0: no check bit (NONE, default); 1: odd check bit (ODD); 2: even check bit (EVEN)
            .parity(parity)
            // 数据位,默认8；可选值为5~8
            // Data bit, default 8; optional value is 5~8
            .dataBits(dataBits)
            // 停止位，默认1；1:1位停止位；2:2位停止位
            // Stop bit, default 1; 1:1 stop bit; 2: 2 stop bit
            .stopBits(stopBits)
            .build()
    }

    val data = flow<ByteArray> {
        while (currentCoroutineContext().isActive) {
            var size: Int
            try {
                val buffer = ByteArray(64)
                size = serialPort.inputStream.read(buffer)
                if (size > 0) {
                    emit(buffer.copyOf(size))
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }.catch { currentCoroutineContext().cancel() }.flowOn(Dispatchers.IO)


    suspend fun write(bytes: ByteArray) {
        withContext(Dispatchers.IO) {
            try {
                serialPort.outputStream.write(bytes)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun close() {
        serialPort.tryClose()
    }

}

