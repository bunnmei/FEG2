package space.webkombinat.feg2.DB.Profile

import kotlinx.coroutines.flow.Flow

class ProfileRepository(
    private val profileDao: ProfileDao
) {
    suspend fun insertProfile(profileEntity: ProfileEntity): Long {
        return profileDao.create(profileEntity)
    }

    fun getAll(): Flow<List<ProfileEntity>> {
        return profileDao.getAll()
    }
}