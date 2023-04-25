package com.br3ant.serialport

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br3ant.utils.hexToBytes
import com.br3ant.utils.toBytes
import com.br3ant.utils.toHexString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.experimental.xor

class MainViewModel : ViewModel() {
    val port = SerialPortFlow("/dev/ttysWK1", 9600, stopBits = 1)

    var sendData by mutableStateOf("")
    var receiveData by mutableStateOf("")

    fun send(hexString: String, onResponse: (response: String) -> Unit = {}) {
        send(hexString.hexToBytes(), onResponse)
    }

    fun send(bytes: ByteArray, onResponse: (response: String) -> Unit = {}) {
        viewModelScope.launch {
            delay(100)
            sendData = bytes.toHexString().uppercase()
            Log.i("MainViewModel", "send -> $sendData")
            receiveData = port.write(bytes).toHexString().uppercase()
            onResponse(receiveData)

            Log.i("MainViewModel", "receiveData -> $receiveData")
        }
    }

    fun sendCmd(cmd: String, data: String, onResponse: (response: String) -> Unit = {}) {
        val len = (data.length / 2 + 1).toLong().toBytes().toHexString()
        val command = "$len$cmd$data"
        val bcc = command.hexToBytes().reduce { acc, byte -> acc.xor(byte) }.toLong().toBytes()
            .toHexString()
        send("0200${command + bcc}", onResponse)
    }


    override fun onCleared() {
        super.onCleared()
        port.close()
    }

}