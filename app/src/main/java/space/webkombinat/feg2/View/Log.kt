package space.webkombinat.feg2.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import space.webkombinat.feg2.DB.Profile.ProfileEntity
import space.webkombinat.feg2.ViewModel.LogViewModel

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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(168.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                RoundedCornerShape(5.dp)
            )
            .clickable {
                click()
            }
    ) {
        Column {
            Row(
                modifier = modifier
                    .height(60.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = modifier.padding(top = 8.dp, start = 8.dp).height(60.dp).weight(1f),
                    text = if (profile.name == null || profile.name == "") {"No name"} else {profile.name!!},
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(
                    modifier
                        .width(60.dp)
                        .height(60.dp)
                        .clickable {
//                            menu.value = true
                        },
                    contentAlignment = Alignment.Center
                ){
//                    Icon(
//                        imageVector = Icons.Default.MoreVert,
//                        contentDescription = "More Vert"
//                    )
                }
            }
            Text(
                text = if (profile.description == null || profile.description == "") {""} else {profile.description!!},
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = modifier.weight(1f))
            Row {
                Spacer(modifier = modifier.weight(1f))
                Text(
                    text = "作成日 ${vm.formatTime(profile.createAt)}",
                    modifier = modifier
                        .padding(end = 8.dp, bottom = 8.dp)
                )
            }
        }
    }
}

