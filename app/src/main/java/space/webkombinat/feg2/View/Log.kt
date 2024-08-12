package space.webkombinat.feg2.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.DB.Profile.ProfileEntity
import space.webkombinat.feg2.ViewModel.LogViewModel
import java.util.Date

@Composable
fun Log(
    vm: LogViewModel,
    modifier: Modifier = Modifier,
    click: (Long) -> Unit
) {
    val lists = vm.profiles.collectAsState(initial = emptyList())
    if (lists.value.isEmpty()) {
        Text(text = "NO DATA")
    } else {
            LazyColumn {
                item {
                    Spacer(modifier = modifier.height(5.dp))
                }
                items(
                    count = lists.value.size,
                    key = {i -> lists.value[i].id},
                    itemContent = {
                        LogPanel(
                            lists.value[it],
                            vm = vm,
                            click = {
                                click(lists.value[it].id)
                            }
                        )
                    }
                )
                item {
                    Spacer(modifier = modifier.height(65.dp))
                }

            }

    }
}

@Composable
fun LogPanel(
    profile: ProfileEntity,
    modifier: Modifier = Modifier,
    click: () -> Unit,
    vm: LogViewModel,
) {

    val menu = remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .height(80.dp)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                RoundedCornerShape(5.dp)
            )
            .clickable {
                click()
            }
    ) {
        Column {
            Text(
                text = profile.name ?: "NO NAME",
                modifier = modifier.padding(start = 10.dp, top = 10.dp)
            )
            Spacer(modifier = modifier.weight(1f))
            Row {
                Spacer(modifier = modifier.weight(1f))
                Text(
                    text = "作成日 ${vm.formatTime(profile.createAt)}",
                    modifier = modifier
                        .background(Color.Red)
                        .padding(bottom = 10.dp, end = 10.dp)
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
                        offset = DpOffset(10.dp, -50.dp),
                        onDismissRequest = {
                            menu.value = false
                        }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                            },
                            text = {
                                Text("オーバーレイ")
                            }
                        )
                        DropdownMenuItem(
                            onClick = {

                            },
                            text = {
                                Text("編集")
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
    //                            vm.removeData()
                                vm.load(profile.id)
                            },
                            text = {
                                Text("削除")
                            }
                        )
                    }
                }
            }
        }
    }
}

