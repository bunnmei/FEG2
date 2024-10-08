package space.webkombinat.feg2.View

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
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
import space.webkombinat.feg2.ViewModel.EditViewModel
import space.webkombinat.feg2.ViewModel.LogDetailViewModel
import space.webkombinat.feg2.ViewModel.LogViewModel
import space.webkombinat.feg2.ViewModel.SettingViewModel



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(
//    tempList: SnapshotStateList<Line>,
//    tempList_BT: SnapshotStateList<Line>,
//    time: MutableState<String>,
//    temp: MutableState<Int>,
//    temp_BT: MutableState<Int>,

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
                startDestination = BottomNavigation.Chart.route,
                enterTransition = { fadeIn(animationSpec = tween(2000)) },
                exitTransition = { fadeOut(animationSpec = tween(2000)) },
                popExitTransition = { fadeOut(animationSpec = tween(2000))},
                popEnterTransition = { fadeIn(animationSpec = tween(2000))  },
            ){
                logScreens(navCont = navCont, toggleBottom = showBtmNav)
                screen(
                    route = BottomNavigation.Chart.route,
                ) {
                        val vm: ChartViewModel = hiltViewModel()
                        Chart(
//                            tempList = tempList,
//                            tempList_BT = tempList_BT,
//                            time = time,
//                            temp = temp,
//                            temp_BT = temp_BT,
                            vm = vm,
                            bottomShow = showBtmNav
                        ) {
                            showBtmNav.value = !showBtmNav.value
                        }

                }

                screen(
                    route = BottomNavigation.Setting.route
                ) {
                    val vm: SettingViewModel = hiltViewModel()
                    Setting(vm = vm)
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
        route = BottomNavigation.LogList.route,
        enterTransition = { fadeIn(animationSpec = tween(200)) },
        exitTransition = { fadeOut(animationSpec = tween(200)) },
        popExitTransition = { fadeOut(animationSpec = tween(200)) },
        popEnterTransition = { fadeIn(animationSpec = tween(200)) },
//        enterTransition = { EnterTransition.None },
//        exitTransition = { ExitTransition.None },
//        popExitTransition = { ExitTransition.None},
//        popEnterTransition = { EnterTransition.None },
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
            it.arguments?.getLong("profileId")?.let { id ->
                LogDetail(
                    vm = vm,
                    bottomShow = toggleBottom,
                    navCont= navCont,
                    str = id
                ){
                    toggleBottom.value = !toggleBottom.value
                }
            }
        }

        screen(
            route = "/edit/{profileId}",
            arguments = listOf(
                navArgument("profileId"){
                    type = NavType.LongType
                }
            )
        ) {
            val vm: EditViewModel = hiltViewModel()
            it.arguments?.getLong("profileId")?.let { id ->
                Edit(
                    vm=vm,
                    id=id,
                    click = {
                        navCont.popBackStack()
                    }
                )
            }

        }
    }
}

//@ExperimentalAnimationApi
fun NavGraphBuilder.screen(
    route: String,
    arguments: List<NamedNavArgument> = listOf(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
//    val animSpec: FiniteAnimationSpec<IntOffset> = tween(200, easing = FastOutSlowInEasing)
    composable(
        route,
        arguments = arguments,
        enterTransition = { fadeIn(animationSpec = tween(200)) },
        exitTransition = { fadeOut(animationSpec = tween(200)) },
        popExitTransition = { fadeOut(animationSpec = tween(200)) },
        popEnterTransition = { fadeIn(animationSpec = tween(200)) },
        content = content
    )
}
