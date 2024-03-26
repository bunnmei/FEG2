package space.webkombinat.feg2.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.DB.Profile.ProfileEntity
import space.webkombinat.feg2.ViewModel.LogViewModel
import java.util.Date

@Composable
fun Log(
    vm: LogViewModel,
    click: (Long) -> Unit
) {
    val lists = vm.profiles.collectAsState(initial = emptyList())
    LazyColumn {
        items(
            count = lists.value.size,
            key = {i -> lists.value[i].id},
            itemContent = {
                LogPanel(
                    lists.value[it],
                    click = {
                        click(lists.value[it].id)
                    }
                )
            }
        )
    }
}

@Composable
fun LogPanel(profile: ProfileEntity, modifier: Modifier = Modifier, click: () -> Unit) {
    Row(
        modifier = modifier
            .height(60.dp)
            .clickable {
                click()
            }
    ) {
        Text(text = profile.name ?: "null")
        Divider(modifier = Modifier.width(15.dp))
        Text(text = Date(profile.createAt).toString())
    }
}

