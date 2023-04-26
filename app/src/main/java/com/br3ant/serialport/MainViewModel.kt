package com.br3ant.serialport

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br3ant.serialport.profile.Error
import com.br3ant.serialport.profile.Request
import com.br3ant.serialport.profile.Response
import com.br3ant.serialport.profile.parse
import com.br3ant.utils.hexToBytes
import com.br3ant.utils.toHexString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var port: SerialPortFlow? = SerialPortFlow("/dev/ttysWK1", 9600, stopBits = 1)

    var sendData by mutableStateOf("")
    var receiveData by mutableStateOf("")

    var portIsConnect by mutableStateOf(port != null)

    fun send(hexString: String, onResponse: (response: Response) -> Unit = {}) {
        send(hexString.hexToBytes(), onResponse)
    }

    fun send(bytes: ByteArray, onResponse: (response: Response) -> Unit = {}) {
        viewModelScope.launch {
            val _port = port
            if (_port == null) {
                onResponse(Error)
                return@launch
            }
            delay(100)
            sendData = bytes.toHexString().uppercase()
            Log.i("MainViewModel", "send -> $sendData")
            val result = _port.write(bytes)
            receiveData = result.toHexString().uppercase()
            onResponse(result.parse())

            Log.i("MainViewModel", "receive -> $receiveData")
        }
    }

    fun sendCmd(request: Request, onResponse: (response: Response) -> Unit = {}) {
        send(request.command(), onResponse)
    }

    fun connect(path: String, baudrate: Int) {
        viewModelScope.launch {
            port?.close()
            port = SerialPortFlow(path, baudrate, stopBits = 1)
            portIsConnect = port != null
        }
    }

    fun disConnect() {
        port?.close()
        port = null
        portIsConnect = port != null
    }


    override fun onCleared() {
        super.onCleared()
        port?.close()
    }

}