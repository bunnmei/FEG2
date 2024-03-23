package space.webkombinat.feg2.View

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import space.webkombinat.feg2.Data.BottomNavigation

@Composable
fun BottomNavigationBar(
    show: MutableState<Boolean>,
    navCont: NavController,
    modifier: Modifier = Modifier,
    items: List<BottomNavigation> = listOf(
        BottomNavigation.LogList,
        BottomNavigation.Chart,
        BottomNavigation.Setting
    ),
    onItemClick: (BottomNavigation) -> Unit
) {
    var selectedItem by remember { mutableStateOf(1) }
    AnimatedVisibility(
        visible = show.value,
        enter = slideInVertically(initialOffsetY = {it}),
        exit = slideOutVertically(targetOffsetY = {it})
    ) {
        NavigationBar(
            modifier = modifier.height(60.dp)
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedItem == index,
                    onClick = {
                        selectedItem = index
                        if (index != 1){
                            show.value = true
                        }
                        onItemClick(item)
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor= MaterialTheme.colorScheme.primary,
                        indicatorColor= MaterialTheme.colorScheme.secondaryContainer,
                        unselectedIconColor= MaterialTheme.colorScheme.outline,
                        unselectedTextColor= MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}