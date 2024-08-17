package space.webkombinat.feg2.DB.Profile

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import space.webkombinat.feg2.DB.Chart.ChartEntity
import space.webkombinat.feg2.DB.ProfileLinkChart

@Dao
interface ProfileDao {
    @Insert
    suspend fun create(profile: ProfileEntity): Long

    @Delete
    suspend fun delete(profile: ProfileEntity)

    @Delete
    suspend fun deleteChart(chart: ChartEntity)

    @Update
    suspend fun update(profile: ProfileEntity)

    @Query("SELECT * FROM profile WHERE id = :id")
    suspend fun getProfile(id: Long): ProfileEntity

    @Query("SELECT * FROM profile order by createAt desc")
    fun getAll(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profile WHERE id = :id")
    suspend fun profileAndChartData(id: Long): ProfileLinkChart?

    @Query("SELECT * FROM profile WHERE id = :id")
    suspend fun profileAndChartDataNotNull(id: Long): ProfileLinkChart

    @Query("SELECT * FROM profile WHERE id = :id")
    fun profileAndChart(id: Long): Flow<List<ProfileLinkChart>>
}