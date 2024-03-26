package space.webkombinat.feg2

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import space.webkombinat.feg2.DB.Chart.ChartDao
import space.webkombinat.feg2.DB.Profile.ProfileDao
import space.webkombinat.feg2.DB.ProfileDatabase
import javax.inject.Singleton

//class AppModule {
//}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ProfileDatabase {
        return Room.databaseBuilder(
            context,
            ProfileDatabase::class.java,
            "profile.db",
        ).build()
    }

    @Provides
    @Singleton
    fun provideProfile(db: ProfileDatabase): ProfileDao {
        return db.profileDao()
    }

//    @Provides
//    @Singleton
//    fun provideChart(db: ProfileDatabase): ChartDao {
//        return db.chartDao()
//    }
}