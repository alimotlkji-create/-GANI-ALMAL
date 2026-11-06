package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET ganiCoins = :newCoins WHERE id = :userId")
    suspend fun updateCoins(userId: String, newCoins: Long)

    @Query("UPDATE users SET level = :level, levelXp = :xp WHERE id = :userId")
    suspend fun updateLevelAndXp(userId: String, level: String, xp: Long)

    @Query("UPDATE users SET avatarFrame = :frame WHERE id = :userId")
    suspend fun updateAvatarFrame(userId: String, frame: String)

    @Query("UPDATE users SET customTitle = :title WHERE id = :userId")
    suspend fun updateCustomTitle(userId: String, title: String)

    @Query("UPDATE users SET themeAccent = :theme WHERE id = :userId")
    suspend fun updateThemeAccent(userId: String, theme: String)

    @Query("UPDATE users SET avatarIcon = :icon WHERE id = :userId")
    suspend fun updateAvatarIcon(userId: String, icon: String)

    @Query("UPDATE users SET isSuspended = :suspended WHERE id = :userId")
    suspend fun setUserSuspended(userId: String, suspended: Boolean)

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getTotalUsersCount(): Int

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)
}

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(transaction: PointTransactionEntity)

    @Query("SELECT * FROM point_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: String): Flow<List<PointTransactionEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM point_transactions WHERE userId = :userId AND referenceId = :refId LIMIT 1)")
    suspend fun hasProcessedReference(userId: String, refId: String): Boolean

    @Query("SELECT SUM(amount) FROM point_transactions WHERE type = 'EARNED'")
    suspend fun getTotalEarnedAll(): Long?

    @Query("SELECT SUM(amount) FROM point_transactions WHERE type = 'SPENT'")
    suspend fun getTotalSpentAll(): Long?

    @Query("DELETE FROM point_transactions WHERE userId = :userId")
    suspend fun deleteForUser(userId: String)
}

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges ORDER BY coinsReward ASC")
    fun getAllChallengesFlow(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE type = :type")
    suspend fun getChallengesByType(type: String): List<ChallengeEntity>

    @Query("SELECT * FROM challenges WHERE id = :id LIMIT 1")
    suspend fun getChallengeById(id: String): ChallengeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    @Delete
    suspend fun deleteChallenge(challenge: ChallengeEntity)

    @Query("DELETE FROM challenges WHERE id = :id")
    suspend fun deleteChallengeById(id: String)
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM user_challenge_progress WHERE userId = :userId")
    fun getProgressForUserFlow(userId: String): Flow<List<UserChallengeProgressEntity>>

    @Query("SELECT * FROM user_challenge_progress WHERE userId = :userId AND challengeId = :challengeId LIMIT 1")
    suspend fun getProgress(userId: String, challengeId: String): UserChallengeProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: UserChallengeProgressEntity)

    @Query("DELETE FROM user_challenge_progress WHERE userId = :userId")
    suspend fun deleteForUser(userId: String)
}

@Dao
interface RewardDao {
    @Query("SELECT * FROM reward_items")
    fun getAllRewardsFlow(): Flow<List<RewardItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewards(rewards: List<RewardItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: RewardItemEntity)

    @Delete
    suspend fun deleteReward(reward: RewardItemEntity)

    @Query("SELECT * FROM reward_items WHERE id = :rewardId LIMIT 1")
    suspend fun getRewardById(rewardId: String): RewardItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserReward(userReward: UserRewardEntity)

    @Query("SELECT rewardId FROM user_rewards WHERE userId = :userId")
    fun getUserOwnedRewardIdsFlow(userId: String): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM user_rewards WHERE userId = :userId AND rewardId = :rewardId LIMIT 1)")
    suspend fun userOwnsReward(userId: String, rewardId: String): Boolean

    @Query("DELETE FROM user_rewards WHERE userId = :userId")
    suspend fun deleteForUser(userId: String)
}

@Dao
interface AdRewardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdRecord(record: AdRewardRecordEntity)

    @Query("SELECT COUNT(*) FROM ad_reward_records WHERE userId = :userId")
    suspend fun getAdWatchesCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM ad_reward_records")
    suspend fun getTotalAdsWatchedAll(): Int

    @Query("SELECT timestamp FROM ad_reward_records WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastAdTimestamp(userId: String): Long?

    @Query("DELETE FROM ad_reward_records WHERE userId = :userId")
    suspend fun deleteForUser(userId: String)
}

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("SELECT * FROM app_reports ORDER BY timestamp DESC")
    fun getAllReportsFlow(): Flow<List<ReportEntity>>

    @Query("UPDATE app_reports SET status = :status WHERE id = :id")
    suspend fun updateReportStatus(id: String, status: String)
}
