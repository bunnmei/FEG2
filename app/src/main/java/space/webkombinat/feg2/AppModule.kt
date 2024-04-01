package space.webkombinat.feg2

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import space.webkombinat.feg2.DB.Chart.ChartDao
import space.webkombinat.feg2.DB.Chart.ChartEntity
import space.webkombinat.feg2.DB.Chart.ChartRepository
import space.webkombinat.feg2.DB.Profile.ProfileDao
import space.webkombinat.feg2.DB.Profile.ProfileRepository
import space.webkombinat.feg2.DB.ProfileDatabase
import space.webkombinat.feg2.Data.Constants
import space.webkombinat.feg2.Data.LoggerState
import space.webkombinat.feg2.Data.LoggerStore
import space.webkombinat.feg2.Service.RunningService
import javax.inject.Singleton

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

    @Provides
    @Singleton
    fun provideLoggerStatus(): LoggerState {
        return LoggerState()
    }

    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }


    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context: Context
    ): NotificationCompat.Builder {

        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notificationPending = PendingIntent.getService(
            context,
            0,
            Intent(context, RunningService::class.java).also { intent ->
                intent.action = RunningService.Action.NOTIFICATION_STOP.toString()
                context.startService(intent)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, "running_channel")
            .setContentTitle("EFG2")
            .setContentText("時間 00:00 温度 000")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .addAction(0, "停止&保存", notificationPending)
            .setContentIntent(resultPendingIntent)
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