package space.webkombinat.feg2.DB.Chart

data class Chart(
    val id: Long,
    val profileId: Long,
    val point_index: Int,
    val temp: Int
)
