package space.webkombinat.feg2.View

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import space.webkombinat.feg2.Data.BottomNavigation


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navCont = rememberNavController()
//    val backStackEntry = navCont.currentBackStackEntryAsState()
    val showBtmNav = remember {
        mutableStateOf(true)
    }
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                show = showBtmNav,
                navCont = navCont,
            ) {
                navCont.navigate(it.route) {
                    popUpTo(navCont.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    ) {
        NavHost(
            navController = navCont,
            startDestination = BottomNavigation.Chart.route
        ){
            composable(
                route = BottomNavigation.LogList.route
            ) {
                Log()
            }

            composable(
                route = BottomNavigation.Chart.route
            ) {
                Chart {
                    showBtmNav.value = !showBtmNav.value
                }
            }

            composable(
                route = BottomNavigation.Setting.route
            ) {
                Setting()
            }
        }
    }
}