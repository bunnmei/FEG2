package space.webkombinat.feg2.DB.Chart

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chart")
data class ChartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "profile_id")
    val profileId: Long,
    val point_index: Int,
    val temp: Int
)
