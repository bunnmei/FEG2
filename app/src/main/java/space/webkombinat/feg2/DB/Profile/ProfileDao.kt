package space.webkombinat.feg2.DB.Profile

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert
    suspend fun create(profile: ProfileEntity): Long

    @Query("SELECT * FROM profile order by createAt desc")
    fun getAll(): Flow<List<ProfileEntity>>
}