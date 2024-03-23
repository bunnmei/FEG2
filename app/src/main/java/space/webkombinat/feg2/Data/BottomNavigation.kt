package space.webkombinat.feg2.Data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SsidChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigation(
    val route: String,
    val icon: ImageVector,
    val label: String,
) {
    object LogList: BottomNavigation(route = "list", icon = Icons.Default.List, label = "LOG")
    object Chart: BottomNavigation(route = "chart", icon = Icons.Default.SsidChart,label = "CHART")
    object Setting: BottomNavigation(route = "setting", icon = Icons.Default.Settings, label = "SETTING")
}