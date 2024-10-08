package space.webkombinat.feg2.DB.Profile

import kotlinx.coroutines.flow.Flow
import space.webkombinat.feg2.DB.Chart.ChartEntity
import space.webkombinat.feg2.DB.ProfileLinkChart

class ProfileRepository(
    private val profileDao: ProfileDao
) {
    suspend fun insertProfile(profileEntity: ProfileEntity): Long {
        return profileDao.create(profileEntity)
    }

    suspend fun deleteProfile(profileEntity: ProfileEntity) {
        profileDao.delete(profileEntity)
    }

    suspend fun deleteChart(chartEntity: ChartEntity) {
        profileDao.deleteChart(chartEntity)
    }

    suspend fun update(profileEntity: ProfileEntity){
        profileDao.update(profileEntity)
    }

    fun getAll(): Flow<List<ProfileEntity>> {
        return profileDao.getAll()
    }

    suspend fun get(id: Long): ProfileEntity {
        return profileDao.getProfile(id)
    }
    suspend fun profileAndChartData(id: Long): ProfileLinkChart? {
        return profileDao.profileAndChartData(id)
    }

    suspend fun profileAndChartDataNotNull(id: Long): ProfileLinkChart {
        return profileDao.profileAndChartDataNotNull(id)
    }

    fun profileAndChart(id: Long): Flow<List<ProfileLinkChart>> {
        return profileDao.profileAndChart(id = id)
    }
}