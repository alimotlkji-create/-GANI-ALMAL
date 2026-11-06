package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        PointTransactionEntity::class,
        ChallengeEntity::class,
        UserChallengeProgressEntity::class,
        RewardItemEntity::class,
        UserRewardEntity::class,
        AdRewardRecordEntity::class,
        ReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun progressDao(): ProgressDao
    abstract fun rewardDao(): RewardDao
    abstract fun adRewardDao(): AdRewardDao
    abstract fun reportDao(): ReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gani_almal_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
