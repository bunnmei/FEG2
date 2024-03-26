package space.webkombinat.feg2.DB.Profile

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import space.webkombinat.feg2.DB.ProfileLinkChart

@Dao
interface ProfileDao {
    @Insert
    suspend fun create(profile: ProfileEntity): Long

    @Query("SELECT * FROM profile order by createAt desc")
    fun getAll(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profile WHERE id = :id")
    fun profileAndChart(id: Long): Flow<List<ProfileLinkChart>>
}