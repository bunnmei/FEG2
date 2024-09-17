package space.webkombinat.feg2.View

import androidx.collection.emptyLongSet
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import space.webkombinat.feg2.Data.LogNavigation
import space.webkombinat.feg2.View.component.ChartBox
import space.webkombinat.feg2.View.component.TempCanvas
import space.webkombinat.feg2.ViewModel.LogDetailViewModel

@Composable
fun LogDetail(
    vm: LogDetailViewModel,
    navCont: NavController,
    bottomShow: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    str: Long,
    click: () -> Unit,
) {
    val uiState by vm.uiState.collectAsState()
    val savedId by vm.saveId.collectAsState(emptyLongSet())
    val height = LocalContext.current.resources.displayMetrics.heightPixels.toFloat()
    var topRange by remember { mutableStateOf(230) }
    var bottomRange by remember { mutableStateOf(70) }
    LaunchedEffect(vm.topRange) {
        vm.topRange.collectLatest {
            topRange = it
            vm.loggerState.max_range.value = it
            vm.setInit()
        }
    }
    LaunchedEffect(vm.bottomRange) {
        vm.bottomRange.collectLatest {
            bottomRange = it
            vm.loggerState.min_range.value = it
            vm.setInit()
        }
    }

    when(uiState) {
        LogDetailViewModel.UiState.Initial,
        LogDetailViewModel.UiState.LoadSuccess -> {
                val list_ET = vm.chartList
                val list_BT = vm.chartList_BT
                val clack_f = vm.clack
                val menu = remember { mutableStateOf(false) }
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = click
                        )
                ){
//                    Text("savedId-☆-${savedId}")
                    ChartBox(
                        tempList = list_ET,
                        tempList_BT = list_BT,
                        clack = clack_f,
                        bottomShow = bottomShow,
                        vm = null
                    )
                    TempCanvas(
                        color = MaterialTheme.colorScheme.primary,
                        topRange = topRange,
                        bottomRange = bottomRange
                    )
                    Box(
                        modifier = modifier.fillMaxSize()
                            .wrapContentSize(Alignment.TopEnd),
                    ){
                        Box(
                            modifier.width(60.dp)
                                .height(60.dp)
//                                    .background(Color.Red)
                                .clickable {
                                    menu.value = true
                                },
                            contentAlignment = Alignment.Center
                        ){
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Vert"
                            )
                        }
                        DropdownMenu(
                            expanded = menu.value,
                            offset = DpOffset(10.dp, 0.dp),
                            onDismissRequest = {
                                menu.value = false
                            }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    vm.saveOverlay()
                                },
                                text = {
                                    Text("オーバーレイ")
                                }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    navCont.navigate(route = "/edit/${str}")
                                },
                                text = {
                                    Text("編集")
                                }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    vm.removeData(str)
                                },
                                text = {
                                    Text("削除")
                                }
                            )
                        }
                    }

                }
        }
        LogDetailViewModel.UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }

        LogDetailViewModel.UiState.Deleting -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("削除中")
            }
        }

        LogDetailViewModel.UiState.Deleted -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    Text("削除完了")
                    Spacer(modifier = modifier.height(30.dp))
                    Button(
                        modifier = modifier.width(180.dp).height(60.dp),
                        onClick = {
                            navCont.popBackStack()
                        }
                    ) {
                        Text("戻る")
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = uiState) {
        when(uiState) {
            LogDetailViewModel.UiState.Initial -> {
                vm.load(height = height)
            }
            LogDetailViewModel.UiState.LoadSuccess -> {}
            LogDetailViewModel.UiState.Loading -> {}
            LogDetailViewModel.UiState.Deleting -> {}
            LogDetailViewModel.UiState.Deleted -> {

            }
        }
    }
}