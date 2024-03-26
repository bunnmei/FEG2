package space.webkombinat.feg2.DB.Chart

class ChartRepository(
    private val chartDao: ChartDao
) {
    suspend fun insertChart(point: ChartEntity) {
        chartDao.create(point)
    }
}