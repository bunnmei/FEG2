package space.webkombinat.feg2.DB.Profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "profile")
data class ProfileEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    var name: String?,

    var description: String?,

    var clack_f: Int?,

    var clack_s: Int?,

    val createAt: Long,
)