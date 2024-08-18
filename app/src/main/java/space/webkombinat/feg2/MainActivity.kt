package space.webkombinat.feg2

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.collection.emptyIntSet
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import space.webkombinat.feg2.Service.RunningService
import space.webkombinat.feg2.View.Navigation
import space.webkombinat.feg2.ViewModel.MainViewModel
import space.webkombinat.feg2.ui.theme.FEG2Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    private var isBound by mutableStateOf(false)
//    private lateinit var runningService: RunningService
//    private val connection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            val binder = service as RunningService.TimerAndTemp
//            runningService = binder.getService()
//            isBound = true
//        }
//        override fun onServiceDisconnected(arg0: ComponentName) {
//            isBound = false
//        }
//    }

    override fun onStart() {
        super.onStart()
//        Intent(this, RunningService::class.java).also {
//            bindService(it, connection, Context.BIND_AUTO_CREATE)
//        }
        val serviceInt = Intent(applicationContext, RunningService::class.java)
        startService(serviceInt)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        setContent {
            val vm = hiltViewModel<MainViewModel>()
            val mode by vm.mode.collectAsState(emptyIntSet())
            FEG2Theme(
                darkTheme = when(mode) {
                    1 -> true
                    2 -> false
                    else -> isSystemInDarkTheme()
                }
            ) {
//                if (isBound){
                        Navigation()
//                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
//        unbindService(connection)
//        isBound = false
    }
}


//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    FEG2Theme {
//        Greeting("Android")
//    }
//}