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
import androidx.compose.foundation.layout.IntrinsicSize
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
import com.br3ant.serialport.profile.Anticoll
import com.br3ant.serialport.profile.Authentication
import com.br3ant.serialport.profile.Device
import com.br3ant.serialport.profile.FindAllCard
import com.br3ant.serialport.profile.FindNotIDLE
import com.br3ant.serialport.profile.LoadKey
import com.br3ant.serialport.profile.Read
import com.br3ant.serialport.profile.SelectCard
import com.br3ant.serialport.ui.theme.AndroidSerialPortAPIFlowTheme
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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Row(
                            modifier = Modifier.align(Alignment.Start),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(IntrinsicSize.Max)
                                    .border(1.dp, MaterialTheme.colors.primary)
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                var path by remember { mutableStateOf("/dev/ttysWK1") }
                                var baudrate by remember { mutableStateOf(9600) }

                                OutlinedTextField(value = path, onValueChange = { value ->
                                    path = value.trim()
                                }, label = { Text(text = "串口地址") })

                                OutlinedTextField(
                                    value = baudrate.toString(),
                                    onValueChange = { value ->
                                        baudrate = value.toInt()
                                    },
                                    label = { Text(text = "比特率") })

                                Text(
                                    text = if (viewModel.portIsConnect) "已连接" else "已断开",
                                    color = Color.Red,
                                    fontSize = 20.sp,
                                    fontStyle = FontStyle.Italic
                                )

                                Row(
                                    modifier = Modifier,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Button(onClick = { viewModel.sendCmd(FindAllCard) }) {
                                        Text(text = "连接")
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Button(onClick = { viewModel.sendCmd(FindNotIDLE) }) {
                                        Text(text = "断开")
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .width(400.dp)
                                    .border(1.dp, MaterialTheme.colors.primary)
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(onClick = { viewModel.sendCmd(Device("40")) }) {
                                    Text(text = "到读卡位")
                                }
                                Button(onClick = { viewModel.sendCmd(Device("42")) }) {
                                    Text(text = "回收")
                                }
                                Button(onClick = { viewModel.sendCmd(Device("43")) }) {
                                    Text(text = "吐卡")
                                }
                                Button(onClick = { viewModel.sendCmd(Device("45")) }) {
                                    Text(text = "查询状态")
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

                        var loadKey by remember { mutableStateOf("FFFFFFFFFFFF") }
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = { viewModel.sendCmd(FindAllCard) }) {
                                Text(text = "寻找所有卡")
                            }
                            Button(onClick = { viewModel.sendCmd(FindNotIDLE) }) {
                                Text(text = "寻找空闲卡")
                            }
//                            var card by remember { mutableStateOf("B1E5AACF") }
                            var card by remember { mutableStateOf("") }
                            Button(onClick = {
                                viewModel.sendCmd(Anticoll) { response ->
                                    card = response.data
                                }
                            }) {
                                Text(text = "防碰撞")
                            }
                            Button(onClick = {
                                if (card.isEmpty()) {
                                    toast(scope, scaffoldState, "请先防碰撞获取卡号")
                                } else {
                                    viewModel.sendCmd(SelectCard(card))
                                }
                            }) {
                                Text(text = "选卡")
                            }
                            Button(onClick = { viewModel.sendCmd(LoadKey(loadKey)) }) {
                                Text(text = "加载秘钥")
                            }
                            Button(onClick = {
                                if (card.isEmpty()) {
                                    toast(scope, scaffoldState, "请先防碰撞获取卡号")
                                } else {
                                    viewModel.sendCmd(Authentication(blockCode, card))
                                }
                            }) {
                                Text(text = "校验秘钥")
                            }

                            Text(text = "内码：")
                            Text(text = card, color = Color.Red)
                        }

                        Spacer(modifier = Modifier.height(20.dp))


                        var blockData by remember { mutableStateOf("") }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            Button(onClick = {
                                viewModel.sendCmd(Read(blockCode, areaCode)) {
                                    blockData = it.data
                                }
                            }) {
                                Text(text = "读卡")
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(text = "块数据：")

                                Button(onClick = {
                                    blockData = ""
                                }) {
                                    Text(text = "清空")
                                }
                            }

                            Text(
                                text = blockData,
                                modifier = Modifier
                                    .width(500.dp)
                                    .height(30.dp)
                                    .border(width = 1.dp, color = Color.Black)
                            )
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