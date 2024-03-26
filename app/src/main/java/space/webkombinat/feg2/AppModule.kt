package space.webkombinat.feg2

import android.content.Context
import android.content.ServiceConnection
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import space.webkombinat.feg2.DB.Chart.ChartDao
import space.webkombinat.feg2.DB.Chart.ChartEntity
import space.webkombinat.feg2.DB.Chart.ChartRepository
import space.webkombinat.feg2.DB.Profile.ProfileDao
import space.webkombinat.feg2.DB.Profile.ProfileRepository
import space.webkombinat.feg2.DB.ProfileDatabase
import javax.inject.Singleton

//class AppModule {
//}

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    fun provideRepoP(db: ProfileDatabase): ProfileRepository {
        return ProfileRepository(db.profileDao())
    }
    @Provides
    fun provideRepoC(db: ProfileDatabase): ChartRepository {
        return ChartRepository(db.chartDao())
    }

}

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

    @Provides
    @Singleton
    fun provideChart(db: ProfileDatabase): ChartDao {
        return db.chartDao()
    }
}