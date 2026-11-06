package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val ganiCoins: Long = 100, // Welcome gift of 100 virtual coins
    val level: String = "مبتدئ",
    val levelXp: Long = 0,
    val avatarIcon: String = "avatar_1",
    val avatarFrame: String = "none",
    val themeAccent: String = "gold",
    val customTitle: String = "مستكشف جديد",
    val isAdmin: Boolean = false,
    val isSuspended: Boolean = false,
    val completedChallengesCount: Int = 0,
    val adsWatchedCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "point_transactions")
data class PointTransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String, // "EARNED" or "SPENT"
    val amount: Long,
    val reason: String,
    val referenceId: String, // deduplication key (e.g. challenge_id_date, reward_id, ad_token)
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey val id: String,
    val type: String, // "DAILY", "WEEKLY", "SPECIAL"
    val title: String,
    val description: String,
    val coinsReward: Long,
    val xpReward: Long = 50,
    val category: String, // "DAILY_LOGIN", "QUIZ", "EDUCATIONAL", "EXPLORE", "STREAK", "GENERAL"
    val targetCount: Int = 1,
    val quizQuestion: String? = null,
    val quizOptions: String? = null, // JSON / pipe-separated: "Option 1|Option 2|Option 3|Option 4"
    val correctOptionIndex: Int? = null,
    val educationalFact: String? = null,
    val isBuiltIn: Boolean = true
)

@Entity(tableName = "user_challenge_progress")
data class UserChallengeProgressEntity(
    @PrimaryKey val id: String, // "${userId}_${challengeId}"
    val userId: String,
    val challengeId: String,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val lastDate: String = "", // YYYY-MM-DD
    val claimedAt: Long = 0L
)

@Entity(tableName = "reward_items")
data class RewardItemEntity(
    @PrimaryKey val id: String,
    val type: String, // "FRAME", "TITLE", "THEME", "BADGE"
    val title: String,
    val description: String,
    val priceCoins: Long,
    val iconName: String,
    val valueKey: String // e.g. "gold_halo", "diamond_crown", "master_title"
)

@Entity(tableName = "user_rewards")
data class UserRewardEntity(
    @PrimaryKey val id: String, // "${userId}_${rewardId}"
    val userId: String,
    val rewardId: String,
    val purchasedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ad_reward_records")
data class AdRewardRecordEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val adUnitId: String,
    val rewardAmount: Long,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val username: String,
    val subject: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING" // "PENDING", "RESOLVED"
)
