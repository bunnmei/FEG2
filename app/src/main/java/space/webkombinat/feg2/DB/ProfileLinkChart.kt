package space.webkombinat.feg2.DB

import androidx.room.Embedded
import androidx.room.Relation
import space.webkombinat.feg2.DB.Chart.ChartEntity
import space.webkombinat.feg2.DB.Profile.ProfileEntity

data class ProfileLinkChart(
    @Embedded
    var profile: ProfileEntity,
    @Relation(parentColumn = "id", entityColumn = "profile_id")
    var chart: List<ChartEntity> = emptyList()
)
