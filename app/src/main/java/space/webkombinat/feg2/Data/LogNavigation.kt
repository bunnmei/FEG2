package space.webkombinat.feg2.Data

sealed class LogNavigation(
    val route: String,
    val label: String
) {
    object LogTop: LogNavigation(route = "logTop", label = "top")
    object LogDetail: LogNavigation(route = "/logDetail/{profileId}", label = "logDetail")
    object LogEdit: LogNavigation(route = "/edit/{profileId}", label = "logEdit")

}