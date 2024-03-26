package space.webkombinat.feg2.DB.Profile

class ProfileRepository(
    private val profileDao: ProfileDao
) {
    suspend fun insertProfile(profileEntity: ProfileEntity): Long {
        return profileDao.create(profileEntity)
    }
}