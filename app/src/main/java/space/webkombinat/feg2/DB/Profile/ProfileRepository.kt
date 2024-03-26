package space.webkombinat.feg2.DB.Profile

import kotlinx.coroutines.flow.Flow
import space.webkombinat.feg2.DB.ProfileLinkChart

class ProfileRepository(
    private val profileDao: ProfileDao
) {
    suspend fun insertProfile(profileEntity: ProfileEntity): Long {
        return profileDao.create(profileEntity)
    }

    fun getAll(): Flow<List<ProfileEntity>> {
        return profileDao.getAll()
    }

    fun profileAndChart(id: Long): Flow<List<ProfileLinkChart>> {
        return profileDao.profileAndChart(id = id)
    }
}