package space.webkombinat.feg2.DB.Profile

import androidx.room.PrimaryKey
import java.util.Date


data class Profile(
    val id: Long = 0,

    var name: String?,

    var description: String?,

    var clack_f: Int?,

    var clack_s: Int?,

    val createAt: Date,
)
