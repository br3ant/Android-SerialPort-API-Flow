package com.br3ant.serialport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br3ant.serialport.ui.theme.AndroidSerialPortAPIFlowTheme
import com.br3ant.utils.toBytes
import com.br3ant.utils.toHexString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSerialPortAPIFlowTheme {
                val scaffoldState = rememberScaffoldState()
                val scope = rememberCoroutineScope()

                Scaffold(scaffoldState = scaffoldState, snackbarHost = {
                    SnackbarHost(it) { data ->
                        Toast(data = data)
                    }

                }) { padding ->

//                    padding.calculateTopPadding()

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Text(
                            text = "串口参数: ${viewModel.port}",
                            color = MaterialTheme.colors.primary,
                            fontSize = 20.sp,
                            fontStyle = FontStyle.Italic
                        )

                        Text(
                            text = "send -> ${viewModel.sendData}",
                            color = MaterialTheme.colors.error,
                            fontSize = 25.sp,
                            fontStyle = FontStyle.Italic
                        )

                        Text(
                            text = "response <- ${viewModel.receiveData}",
                            color = MaterialTheme.colors.error,
                            fontSize = 25.sp,
                            fontStyle = FontStyle.Italic
                        )


                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {

                                Button(onClick = { viewModel.send("02 00 02 2F 40 6D") }) {
                                    Text(text = "到读卡位：02 00 02 2F 40 6D")
                                }

                                Button(onClick = { viewModel.send("02 00 02 2F 43 6E") }) {
                                    Text(text = "吐卡：02 00 02 2F 43 6E")
                                }

                                Button(onClick = { viewModel.send("02 00 02 2F 42 6F") }) {
                                    Text(text = "回收：02 00 02 2F 42 6F")
                                }

                                Button(onClick = { viewModel.send("02 00 02 2F 45 68") }) {
                                    Text(text = "查询状态：02 00 02 2F 45 68")
                                }

                                Button(onClick = { viewModel.send("02 00 02 31 52 61") }) {
                                    Text(text = "寻卡：02 00 02 31 52 61")
                                }
                                Button(onClick = { viewModel.send("02 00 02 32 93 A3") }) {
                                    Text(text = "防碰撞：02 00 02 32 93 A3")
                                }
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                var input by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = input,
                                    onValueChange = { input = it },
                                    label = {
                                        Text(text = "请输入十六进制命令")
                                    })

                                Button(onClick = {
                                    if (input.isEmpty() || input.length % 2 != 0) {
                                        toast(scope, scaffoldState, "请输入正确十六进制的命令")
                                    } else {
                                        viewModel.send(input)
                                    }
                                }) {
                                    Text(text = "发送")
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            LabelText(
                                text = viewModel.sendData,
                                label = "发送数据",
                                modifier = Modifier
                                    .height(100.dp)
                                    .weight(1f)
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            LabelText(
                                text = viewModel.receiveData,
                                label = "接收数据",
                                modifier = Modifier
                                    .height(100.dp)
                                    .weight(1f)
                            )
                        }

                        var loadKey by remember { mutableStateOf("FFFFFFFFFF") }
                        var areaCode by remember { mutableStateOf(1) }
                        var blockCode by remember { mutableStateOf(0) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            OutlinedTextField(value = loadKey, onValueChange = { value ->
                                loadKey = value.trim()
                            }, label = { Text(text = "秘钥") })

                            OutlinedTextField(
                                value = areaCode.toString(),
                                onValueChange = { value ->
                                    areaCode = value.trim().toInt()
                                },
                                label = { Text(text = "区号(1-4)") })

                            OutlinedTextField(
                                value = blockCode.toString(),
                                onValueChange = { value ->
                                    blockCode = value.trim().toInt()
                                },
                                label = { Text(text = "块号(0-64)") })
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(onClick = { viewModel.sendCmd("31", "52") }) {
                                Text(text = "寻找所有卡")
                            }
                            Button(onClick = { viewModel.sendCmd("31", "26") }) {
                                Text(text = "寻找空闲卡")
                            }
                            var card by remember { mutableStateOf("") }
                            Button(onClick = {
                                viewModel.sendCmd("32", "93") { response ->
                                    card = response.takeIf { it.startsWith("00") }?.substring(2, 10)
                                        ?: ""
                                }
                            }) {
                                Text(text = "防碰撞")
                            }
                            Button(onClick = {
                                if (card.length != 8) {
                                    toast(scope, scaffoldState, "请先防碰撞获取卡号")
                                } else {
                                    viewModel.sendCmd("33", "93$card")
                                }
                            }) {
                                Text(text = "选卡")
                            }
                            Button(onClick = { viewModel.sendCmd("35", loadKey) }) {
                                Text(text = "加载秘钥")
                            }
                            Button(onClick = {
                                if (card.length != 8) {
                                    toast(scope, scaffoldState, "请先防碰撞获取卡号")
                                } else {
                                    viewModel.sendCmd(
                                        "37",
                                        "60${blockCode.toLong().toBytes().toHexString()}$card"
                                    )
                                }
                            }) {
                                Text(text = "校验秘钥")
                            }
                            Button(onClick = {
                                viewModel.sendCmd(
                                    "38",
                                    "${blockCode.toLong().toBytes().toHexString()}${
                                        areaCode.toLong().toBytes().toHexString()
                                    }"
                                )
                            }) {
                                Text(text = "读卡")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Toast(data: SnackbarData) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Snackbar(
            modifier = Modifier
                .wrapContentHeight()
                .width(200.dp)
        ) {
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "",
                    tint = Color.White
                )
                Text(
                    text = data.message,
                    modifier = Modifier.padding(start = 10.dp),
                    style = TextStyle(color = Color.White, fontSize = 20.sp)
                )
            }
        }
    }
}

@Composable
fun LabelText(text: String, label: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(text = label, color = Color.Red)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
                .border(BorderStroke(1.dp, Color.Black))
        ) {
            Text(text = text)
        }
    }
}

fun toast(scope: CoroutineScope, state: ScaffoldState, text: String) {
    scope.launch {
        state.snackbarHostState.showSnackbar(text)
    }
}