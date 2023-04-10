package com.br3ant.serialport

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val inPort = SerialPortFlow("/dev/ttysWK1", 115200)
    val outPort = SerialPortFlow("/dev/ttysWK2", 115200)


}