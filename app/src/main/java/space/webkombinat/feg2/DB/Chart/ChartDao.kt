package space.webkombinat.feg2.DB.Chart

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface ChartDao {
    @Insert
    suspend fun create(point: ChartEntity): Long
}