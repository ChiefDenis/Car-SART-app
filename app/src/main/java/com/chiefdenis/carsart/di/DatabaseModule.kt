package com.chiefdenis.carsart.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chiefdenis.carsart.data.database.AppDatabase
import com.chiefdenis.carsart.data.database.VehicleDao
import com.chiefdenis.carsart.data.database.ServiceRecordDao
import com.chiefdenis.carsart.data.database.MaintenanceTaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Since this migration is just adding a new table, Room will handle it.
            // No specific SQL needed here.
        }
    }

    @Provides
    @Singleton
    fun providePassphrase(): () -> ByteArray {
        // TODO: Replace with a secure key provider
        return { SQLiteDatabase.getBytes("default-passphrase".toCharArray()) }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        passphraseProvider: () -> ByteArray
    ): AppDatabase {
        val factory = SupportFactory(passphraseProvider())
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .openHelperFactory(factory)
        .addMigrations(MIGRATION_1_2)
        .build()
    }

    @Provides
    fun provideVehicleDao(appDatabase: AppDatabase): VehicleDao {
        return appDatabase.vehicleDao()
    }

    @Provides
    fun provideServiceRecordDao(appDatabase: AppDatabase): ServiceRecordDao {
        return appDatabase.serviceRecordDao()
    }

    @Provides
    fun provideMaintenanceTaskDao(appDatabase: AppDatabase): MaintenanceTaskDao {
        return appDatabase.maintenanceTaskDao()
    }
}
