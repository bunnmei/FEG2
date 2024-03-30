package space.webkombinat.feg2.View

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import space.webkombinat.feg2.Data.BottomNavigation
import space.webkombinat.feg2.Data.LogNavigation
import space.webkombinat.feg2.Service.Line
import space.webkombinat.feg2.ViewModel.ChartViewModel
import space.webkombinat.feg2.ViewModel.LogDetailViewModel
import space.webkombinat.feg2.ViewModel.LogViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(
    timeStr: String,
    tempStr: String,
    tempList: SnapshotStateList<Line>
) {
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
            logScreens(navCont = navCont, toggleBottom = showBtmNav)
            composable(
                route = BottomNavigation.Chart.route
            ) {
                    val vm: ChartViewModel = hiltViewModel()
                    Chart(
                        timeStr =timeStr,
                        tempStr = tempStr,
                        tempList = tempList,
                        vm = vm,
                    ) {
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

fun NavGraphBuilder.logScreens(
    navCont: NavController,
    toggleBottom: MutableState<Boolean>
){
    navigation(
        startDestination = LogNavigation.LogTop.route,
        route = BottomNavigation.LogList.route
    ) {
        composable(
            route = LogNavigation.LogTop.route
        ) {
            val vm: LogViewModel = hiltViewModel()

            Log(
                vm = vm,
                click = {
                        navCont.navigate("/logDetail/${it}")
                }
            )
        }

        composable(
            route = "/logDetail/{profileId}",
            arguments = listOf(
                navArgument("profileId"){
                    type = NavType.LongType
                }
            )
        ) {
            val vm: LogDetailViewModel = hiltViewModel()
            LogDetail(vm = vm){
                toggleBottom.value = !toggleBottom.value
            }
        }
    }
}