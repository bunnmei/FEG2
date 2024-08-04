package space.webkombinat.feg2.View

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun Navigation(
    tempList: SnapshotStateList<Line>,
    time: MutableState<String>,
    temp: MutableState<Int>,
) {
    val navCont = rememberNavController()
    val backStackEntry = navCont.currentBackStackEntryAsState()
    val showBtmNav = remember {
        mutableStateOf(true)
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                show = showBtmNav,
                navCont = navCont,
            ) {
                navCont.navigate(
                    route = it.route,
                ) {
                    println("build navig ${backStackEntry.value?.destination?.route}")
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
            screen(
                route = BottomNavigation.Chart.route,
            ) {
                    val vm: ChartViewModel = hiltViewModel()
                    Chart(
                        tempList = tempList,
                        time = time,
                        temp = temp,
                        vm = vm,
                        bottomShow = showBtmNav
                    ) {
                        showBtmNav.value = !showBtmNav.value
                    }

            }

            screen(
                route = BottomNavigation.Setting.route
            ) {
                Setting()
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.logScreens(
    navCont: NavController,
    toggleBottom: MutableState<Boolean>
){
    navigation(
        startDestination = LogNavigation.LogTop.route,
        route = BottomNavigation.LogList.route
    ) {
        screen(
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

        screen(
            route = "/logDetail/{profileId}",
            arguments = listOf(
                navArgument("profileId"){
                    type = NavType.LongType
                }
            )
        ) {
            val vm: LogDetailViewModel = hiltViewModel()
            LogDetail(vm = vm, bottomShow = toggleBottom){
                toggleBottom.value = !toggleBottom.value
            }
        }
    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.screen(
    route: String,
    arguments: List<NamedNavArgument> = listOf(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
//    val animSpec: FiniteAnimationSpec<IntOffset> = tween(200, easing = FastOutSlowInEasing)
    composable(
        route,
        arguments = arguments,
        enterTransition = null,
        popEnterTransition = null,
        exitTransition = null,
        popExitTransition = null,
        content = content
    )
}
