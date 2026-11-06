package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class GaniRepository(private val database: AppDatabase) {
    private val userDao = database.userDao()
    private val transactionDao = database.transactionDao()
    private val challengeDao = database.challengeDao()
    private val progressDao = database.progressDao()
    private val rewardDao = database.rewardDao()
    private val adRewardDao = database.adRewardDao()
    private val reportDao = database.reportDao()

    companion object {
        fun hashPassword(password: String): String {
            val salt = "GANI_ALMAL_SECURE_SALT_2026"
            val bytes = MessageDigest.getInstance("SHA-256").digest((password + salt).toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun getTodayString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(Date())
        }

        fun calculateLevelInfo(xp: Long): Pair<String, Float> {
            return when {
                xp < 100 -> Pair("مبتدئ", xp / 100f)
                xp < 300 -> Pair("نشيط", (xp - 100) / 200f)
                xp < 700 -> Pair("متقدم", (xp - 300) / 400f)
                xp < 1500 -> Pair("محترف", (xp - 700) / 800f)
                xp < 3000 -> Pair("خبير", (xp - 1500) / 1500f)
                else -> Pair("أسطورة", 1.0f)
            }
        }

        fun getNextLevelTarget(xp: Long): Long {
            return when {
                xp < 100 -> 100
                xp < 300 -> 300
                xp < 700 -> 700
                xp < 1500 -> 1500
                xp < 3000 -> 3000
                else -> 3000
            }
        }
    }

    // --- Authentication ---

    suspend fun registerUser(username: String, email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val trimmedUsername = username.trim()

        if (trimmedUsername.length < 3) {
            return@withContext Result.failure(Exception("اسم المستخدم يجب أن يكون 3 أحرف على الأقل"))
        }
        if (password.length < 6) {
            return@withContext Result.failure(Exception("كلمة المرور يجب أن تكون 6 خانات على الأقل"))
        }
        if (trimmedEmail.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return@withContext Result.failure(Exception("يرجى إدخال بريد إلكتروني صالح"))
        }

        val existingUser = if (trimmedEmail.isNotEmpty()) userDao.getUserByEmail(trimmedEmail) else null
        if (existingUser != null) {
            return@withContext Result.failure(Exception("البريد الإلكتروني مسجل بالفعل"))
        }

        val existingUsername = userDao.getUserByUsername(trimmedUsername)
        if (existingUsername != null) {
            return@withContext Result.failure(Exception("اسم المستخدم مستخدم مسبقاً"))
        }

        val totalUsers = userDao.getTotalUsersCount()
        // If first user ever or specific admin email, grant admin privilege
        val isAdmin = totalUsers == 0 || trimmedEmail == "admin@gani.com"

        val userId = UUID.randomUUID().toString()
        val newUser = UserEntity(
            id = userId,
            username = trimmedUsername,
            email = trimmedEmail,
            passwordHash = hashPassword(password),
            ganiCoins = 100L, // 100 welcome virtual coins
            level = "مبتدئ",
            levelXp = 0L,
            isAdmin = isAdmin,
            avatarIcon = "avatar_1"
        )

        userDao.insertUser(newUser)

        // Record welcome points transaction
        transactionDao.insertTransaction(
            PointTransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                type = "EARNED",
                amount = 100L,
                reason = "مكافأة الانضمام والترحيب الافتراضية",
                referenceId = "welcome_bonus_$userId",
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(newUser)
    }

    suspend fun loginUser(identifier: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val trimmed = identifier.trim().lowercase()
        val user = userDao.getUserByEmail(trimmed) ?: userDao.getUserByUsername(identifier.trim())

        if (user == null) {
            return@withContext Result.failure(Exception("الحساب غير موجود، يرجى التأكد من البيانات"))
        }

        if (user.isSuspended) {
            return@withContext Result.failure(Exception("تم إيقاف هذا الحساب لمخالفة شروط الاستخدام"))
        }

        if (user.passwordHash != hashPassword(password)) {
            return@withContext Result.failure(Exception("كلمة المرور غير صحيحة"))
        }

        Result.success(user)
    }

    suspend fun resetPassword(identifier: String, newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        val trimmed = identifier.trim().lowercase()
        val user = userDao.getUserByEmail(trimmed) ?: userDao.getUserByUsername(identifier.trim())
            ?: return@withContext Result.failure(Exception("لم يتم العثور على الحساب"))

        if (newPassword.length < 6) {
            return@withContext Result.failure(Exception("كلمة المرور الجديدة يجب أن تكون 6 أحرف على الأقل"))
        }

        userDao.updateUser(user.copy(passwordHash = hashPassword(newPassword)))
        Result.success(Unit)
    }

    fun observeUser(userId: String): Flow<UserEntity?> = userDao.observeUserById(userId)

    suspend fun getUser(userId: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserById(userId)
    }

    // --- Points & Transactions ---

    fun observeTransactions(userId: String): Flow<List<PointTransactionEntity>> =
        transactionDao.getTransactionsForUser(userId)

    suspend fun awardPoints(
        userId: String,
        amount: Long,
        xp: Long,
        reason: String,
        referenceId: String
    ): Result<Long> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: return@withContext Result.failure(Exception("المستخدم غير موجود"))

        // Prevent duplicate processing
        if (transactionDao.hasProcessedReference(userId, referenceId)) {
            return@withContext Result.failure(Exception("تم احتساب هذه المكافأة مسبقاً لمنع التكرار"))
        }

        val newCoins = user.ganiCoins + amount
        val newXp = user.levelXp + xp
        val (newLevel, _) = calculateLevelInfo(newXp)

        userDao.updateCoins(userId, newCoins)
        userDao.updateLevelAndXp(userId, newLevel, newXp)

        transactionDao.insertTransaction(
            PointTransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                type = "EARNED",
                amount = amount,
                reason = reason,
                referenceId = referenceId,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(newCoins)
    }

    suspend fun spendPoints(
        userId: String,
        amount: Long,
        reason: String,
        referenceId: String
    ): Result<Long> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: return@withContext Result.failure(Exception("المستخدم غير موجود"))

        if (user.ganiCoins < amount) {
            return@withContext Result.failure(Exception("رصيد GANI COINS غير كافٍ لإتمام العملية"))
        }

        val newCoins = user.ganiCoins - amount
        userDao.updateCoins(userId, newCoins)

        transactionDao.insertTransaction(
            PointTransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                type = "SPENT",
                amount = amount,
                reason = reason,
                referenceId = referenceId,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(newCoins)
    }

    // --- Challenges & Progress ---

    fun observeChallenges(): Flow<List<ChallengeEntity>> = challengeDao.getAllChallengesFlow()

    fun observeUserProgress(userId: String): Flow<List<UserChallengeProgressEntity>> =
        progressDao.getProgressForUserFlow(userId)

    suspend fun getChallenge(challengeId: String): ChallengeEntity? = withContext(Dispatchers.IO) {
        challengeDao.getChallengeById(challengeId)
    }

    suspend fun completeAndClaimChallenge(
        userId: String,
        challengeId: String,
        verifiedSuccess: Boolean
    ): Result<Pair<Long, Boolean>> = withContext(Dispatchers.IO) {
        if (!verifiedSuccess) {
            return@withContext Result.failure(Exception("لم يتم استيفاء شروط التحدي بنجاح"))
        }

        val challenge = challengeDao.getChallengeById(challengeId)
            ?: return@withContext Result.failure(Exception("التحدي غير موجود"))

        val today = getTodayString()
        val progressId = "${userId}_${challengeId}"
        val existingProgress = progressDao.getProgress(userId, challengeId)

        // For daily challenges, prevent claiming more than once per day
        if (challenge.type == "DAILY" && existingProgress?.isClaimed == true && existingProgress.lastDate == today) {
            return@withContext Result.failure(Exception("لقد أكملت هذا التحدي اليوم، عد غداً لتحديات جديدة"))
        }

        if (challenge.type == "WEEKLY" && existingProgress?.isClaimed == true) {
            return@withContext Result.failure(Exception("تم استلام مكافأة هذا التحدي الأسبوعي مسبقاً"))
        }

        val refId = "challenge_${challengeId}_${if (challenge.type == "DAILY") today else "once"}"
        val awardResult = awardPoints(
            userId = userId,
            amount = challenge.coinsReward,
            xp = challenge.xpReward,
            reason = "إكمال تحدي: ${challenge.title}",
            referenceId = refId
        )

        if (awardResult.isFailure) {
            return@withContext Result.failure(awardResult.exceptionOrNull() ?: Exception("فشل احتساب المكافأة"))
        }

        // Update progress
        progressDao.insertOrUpdateProgress(
            UserChallengeProgressEntity(
                id = progressId,
                userId = userId,
                challengeId = challengeId,
                currentCount = (existingProgress?.currentCount ?: 0) + 1,
                isCompleted = true,
                isClaimed = true,
                lastDate = today,
                claimedAt = System.currentTimeMillis()
            )
        )

        val user = userDao.getUserById(userId)
        val oldLevel = user?.level ?: "مبتدئ"
        val (newLevel, _) = calculateLevelInfo(user?.levelXp ?: 0)
        val leveledUp = newLevel != oldLevel

        // Increment completed challenges count
        userDao.updateUser(user!!.copy(completedChallengesCount = user.completedChallengesCount + 1))

        Result.success(Pair(challenge.coinsReward, leveledUp))
    }

    // --- Ad Rewards ---

    suspend fun recordAndAwardAd(userId: String, adUnitId: String, rewardAmount: Long): Result<Long> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: return@withContext Result.failure(Exception("المستخدم غير موجود"))

        val now = System.currentTimeMillis()
        val lastTimestamp = adRewardDao.getLastAdTimestamp(userId)

        // Reasonable cooldown: at least 15 seconds between ad rewards to prevent spam/abuse
        if (lastTimestamp != null && now - lastTimestamp < 15_000) {
            return@withContext Result.failure(Exception("يرجى الانتظار بضع ثوانٍ قبل مشاهدة إعلان آخر"))
        }

        val refId = "ad_reward_${userId}_${now}"
        val awardResult = awardPoints(
            userId = userId,
            amount = rewardAmount,
            xp = 20L,
            reason = "مكافأة مشاهدة إعلان اختياري",
            referenceId = refId
        )

        if (awardResult.isFailure) {
            return@withContext Result.failure(awardResult.exceptionOrNull() ?: Exception("فشل احتساب المكافأة"))
        }

        adRewardDao.insertAdRecord(
            AdRewardRecordEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                adUnitId = adUnitId,
                rewardAmount = rewardAmount,
                timestamp = now
            )
        )

        userDao.updateUser(user.copy(adsWatchedCount = user.adsWatchedCount + 1))
        Result.success(rewardAmount)
    }

    // --- Rewards Store ---

    fun observeRewards(): Flow<List<RewardItemEntity>> = rewardDao.getAllRewardsFlow()

    fun observeUserOwnedRewards(userId: String): Flow<List<String>> =
        rewardDao.getUserOwnedRewardIdsFlow(userId)

    suspend fun purchaseReward(userId: String, rewardId: String): Result<RewardItemEntity> = withContext(Dispatchers.IO) {
        val reward = rewardDao.getRewardById(rewardId)
            ?: return@withContext Result.failure(Exception("المكافأة غير متوفرة"))

        if (rewardDao.userOwnsReward(userId, rewardId)) {
            return@withContext Result.failure(Exception("أنت تمتلك هذه المكافأة بالفعل في حسابك"))
        }

        val spendResult = spendPoints(
            userId = userId,
            amount = reward.priceCoins,
            reason = "شراء ${reward.title} من المتجر الرقمي",
            referenceId = "reward_purchase_${userId}_${rewardId}"
        )

        if (spendResult.isFailure) {
            return@withContext Result.failure(spendResult.exceptionOrNull() ?: Exception("فشل الشراء"))
        }

        rewardDao.insertUserReward(
            UserRewardEntity(
                id = "${userId}_${rewardId}",
                userId = userId,
                rewardId = rewardId,
                purchasedAt = System.currentTimeMillis()
            )
        )

        // Automatically equip purchased item
        when (reward.type) {
            "FRAME" -> userDao.updateAvatarFrame(userId, reward.valueKey)
            "TITLE" -> userDao.updateCustomTitle(userId, reward.title)
            "THEME" -> userDao.updateThemeAccent(userId, reward.valueKey)
        }

        Result.success(reward)
    }

    suspend fun equipReward(userId: String, reward: RewardItemEntity): Result<Unit> = withContext(Dispatchers.IO) {
        if (!rewardDao.userOwnsReward(userId, reward.id)) {
            return@withContext Result.failure(Exception("لم تقم بشراء هذا العنصر بعد"))
        }
        when (reward.type) {
            "FRAME" -> userDao.updateAvatarFrame(userId, reward.valueKey)
            "TITLE" -> userDao.updateCustomTitle(userId, reward.title)
            "THEME" -> userDao.updateThemeAccent(userId, reward.valueKey)
        }
        Result.success(Unit)
    }

    suspend fun updateAvatarIcon(userId: String, icon: String): Result<Unit> = withContext(Dispatchers.IO) {
        userDao.updateAvatarIcon(userId, icon)
        Result.success(Unit)
    }

    suspend fun deleteUserAccount(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        transactionDao.deleteForUser(userId)
        progressDao.deleteForUser(userId)
        rewardDao.deleteForUser(userId)
        adRewardDao.deleteForUser(userId)
        userDao.deleteUser(userId)
        Result.success(Unit)
    }

    // --- Admin Operations ---

    fun observeAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsersFlow()
    fun observeAllReports(): Flow<List<ReportEntity>> = reportDao.getAllReportsFlow()

    suspend fun getAdminStats(): Map<String, Any> = withContext(Dispatchers.IO) {
        val totalUsers = userDao.getTotalUsersCount()
        val totalEarned = transactionDao.getTotalEarnedAll() ?: 0L
        val totalSpent = transactionDao.getTotalSpentAll() ?: 0L
        val totalAds = adRewardDao.getTotalAdsWatchedAll()
        mapOf(
            "totalUsers" to totalUsers,
            "totalEarned" to totalEarned,
            "totalSpent" to totalSpent,
            "totalAdsWatched" to totalAds
        )
    }

    suspend fun addCustomChallenge(challenge: ChallengeEntity): Result<Unit> = withContext(Dispatchers.IO) {
        challengeDao.insertChallenge(challenge)
        Result.success(Unit)
    }

    suspend fun updateChallenge(challenge: ChallengeEntity): Result<Unit> = withContext(Dispatchers.IO) {
        challengeDao.updateChallenge(challenge)
        Result.success(Unit)
    }

    suspend fun deleteChallenge(challengeId: String): Result<Unit> = withContext(Dispatchers.IO) {
        challengeDao.deleteChallengeById(challengeId)
        Result.success(Unit)
    }

    suspend fun addCustomReward(reward: RewardItemEntity): Result<Unit> = withContext(Dispatchers.IO) {
        rewardDao.insertReward(reward)
        Result.success(Unit)
    }

    suspend fun setUserSuspended(userId: String, suspended: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        userDao.setUserSuspended(userId, suspended)
        Result.success(Unit)
    }

    suspend fun submitReport(userId: String, username: String, subject: String, message: String): Result<Unit> = withContext(Dispatchers.IO) {
        reportDao.insertReport(
            ReportEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                username = username,
                subject = subject,
                message = message
            )
        )
        Result.success(Unit)
    }

    // --- Database Initialization & Seeding ---

    suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingChallenges = challengeDao.getAllChallengesFlow().firstOrNull()
        if (existingChallenges.isNullOrEmpty()) {
            val defaultChallenges = listOf(
                ChallengeEntity(
                    id = "c_daily_login",
                    type = "DAILY",
                    title = "تسجيل الدخول اليومي",
                    description = "افتح تطبيق جني المال يومياً وسجل حضورك لجمع نقاطك اليومية",
                    coinsReward = 20,
                    xpReward = 30,
                    category = "DAILY_LOGIN"
                ),
                ChallengeEntity(
                    id = "c_cultural_quiz",
                    type = "DAILY",
                    title = "حل السؤال الثقافي اليومي",
                    description = "أجب بشكل صحيح عن سؤال الثقافة العربية والمعلومات العامة",
                    coinsReward = 35,
                    xpReward = 50,
                    category = "QUIZ",
                    quizQuestion = "ما هي عاصمة الدولة العباسية الأولى قبل بغداد؟",
                    quizOptions = "الكوفة|الأنبار|دمشق|البصرة",
                    correctOptionIndex = 1
                ),
                ChallengeEntity(
                    id = "c_educational_task",
                    type = "DAILY",
                    title = "إكمال مهمة تعليمية مالية",
                    description = "اقرأ معلومة اليوم حول الادخار الذكي وإدارة الميزانية الشخصية",
                    coinsReward = 25,
                    xpReward = 40,
                    category = "EDUCATIONAL",
                    educationalFact = "قاعدة 50/30/20 المالية: خصص 50% من دخلك للاحتياجات الأساسية، و30% للرغبات الترفيهية، و20% للادخار والاستثمار لمستقبلك المالي الآمن."
                ),
                ChallengeEntity(
                    id = "c_general_quiz",
                    type = "DAILY",
                    title = "اختبار المعلومات العامة",
                    description = "تحدَّ ذكاءك في سؤال الجغرافيا والعلوم العامة اليوم",
                    coinsReward = 30,
                    xpReward = 45,
                    category = "QUIZ",
                    quizQuestion = "أي كوكب في المجموعة الشمسية يُلقب بـ 'الكوكب الأحمر'؟",
                    quizOptions = "المشتري|الزهرة|المريخ|عطارد",
                    correctOptionIndex = 2
                ),
                ChallengeEntity(
                    id = "c_explore_section",
                    type = "DAILY",
                    title = "استكشاف متجر المكافآت",
                    description = "تصفح متجر المكافآت الرقمية الداخلية وتعرف على المزايا والإطارات الحصرية",
                    coinsReward = 15,
                    xpReward = 25,
                    category = "EXPLORE"
                ),
                ChallengeEntity(
                    id = "c_weekly_master",
                    type = "WEEKLY",
                    title = "بطل التحديات الأسبوعي",
                    description = "أكمل ما لا يقل عن 7 تحديات مختلفة خلال هذا الأسبوع",
                    coinsReward = 120,
                    xpReward = 200,
                    category = "STREAK",
                    targetCount = 7
                ),
                ChallengeEntity(
                    id = "c_weekly_level_up",
                    type = "WEEKLY",
                    title = "الارتقاء إلى المستوى القادم",
                    description = "اجمع 300 نقطة خبرة XP للارتقاء في المستويات والحصول على شارة ذهبية جديدة",
                    coinsReward = 150,
                    xpReward = 250,
                    category = "GENERAL",
                    targetCount = 300
                )
            )
            challengeDao.insertChallenges(defaultChallenges)
        }

        val existingRewards = rewardDao.getAllRewardsFlow().firstOrNull()
        if (existingRewards.isNullOrEmpty()) {
            val defaultRewards = listOf(
                RewardItemEntity(
                    id = "r_frame_gold_halo",
                    type = "FRAME",
                    title = "إطار الهالة الذهبية",
                    description = "إطار ذهبي مشع فخم يحيط بصورتك الشخصية في التطبيق",
                    priceCoins = 150,
                    iconName = "military_tech",
                    valueKey = "gold_halo"
                ),
                RewardItemEntity(
                    id = "r_frame_diamond_crown",
                    type = "FRAME",
                    title = "إطار التاج الماسي",
                    description = "رمز الفخامة والتميز لأصحاب الإنجازات الرفيعة",
                    priceCoins = 280,
                    iconName = "workspace_premium",
                    valueKey = "diamond_crown"
                ),
                RewardItemEntity(
                    id = "r_frame_royal_shield",
                    type = "FRAME",
                    title = "إطار الدرع الملكي",
                    description = "درع برونزي ذهبي محاط بنجوم لامعة يعكس قوة حضورك",
                    priceCoins = 220,
                    iconName = "shield",
                    valueKey = "royal_shield"
                ),
                RewardItemEntity(
                    id = "r_frame_neon_aura",
                    type = "FRAME",
                    title = "إطار النيون السماوي",
                    description = "توهج نيون أزرق وسماوي عصري يمنح حسابك طابعاً مستقبلياً",
                    priceCoins = 180,
                    iconName = "auto_awesome",
                    valueKey = "neon_aura"
                ),
                RewardItemEntity(
                    id = "r_title_challenges_master",
                    type = "TITLE",
                    title = "سيد التحديات",
                    description = "لقب فخري يظهر تحت اسمك مباشرة في حسابك الشخصي",
                    priceCoins = 120,
                    iconName = "emoji_events",
                    valueKey = "title_challenges_master"
                ),
                RewardItemEntity(
                    id = "r_title_points_hunter",
                    type = "TITLE",
                    title = "صائد النقاط",
                    description = "لقب رنان للمستخدمين النشيطين الذين يقتنصون كل فرصة",
                    priceCoins = 100,
                    iconName = "toll",
                    valueKey = "title_points_hunter"
                ),
                RewardItemEntity(
                    id = "r_title_knowledge_knight",
                    type = "TITLE",
                    title = "فارس المعرفة",
                    description = "لقب مخصص لمن يتفوقون في الإجابة على الأسئلة والاختبارات",
                    priceCoins = 160,
                    iconName = "psychology",
                    valueKey = "title_knowledge_knight"
                ),
                RewardItemEntity(
                    id = "r_title_golden_legend",
                    type = "TITLE",
                    title = "الأسطورة الذهبية",
                    description = "أرفع لقب داخل تطبيق جني المال، مخصص لرواد القمة",
                    priceCoins = 450,
                    iconName = "stars",
                    valueKey = "title_golden_legend"
                ),
                RewardItemEntity(
                    id = "r_theme_amber",
                    type = "THEME",
                    title = "سمة العنبر والذهب",
                    description = "تعديل درجات الإضاءة والأزرار إلى الذهبي الملكي البراق",
                    priceCoins = 200,
                    iconName = "palette",
                    valueKey = "theme_amber"
                ),
                RewardItemEntity(
                    id = "r_theme_sapphire",
                    type = "THEME",
                    title = "سمة الياقوت الأزرق",
                    description = "تخصيص لمسات الواجهة ببريق الياقوت الأزرق الليلي الفخم",
                    priceCoins = 200,
                    iconName = "diamond",
                    valueKey = "theme_sapphire"
                )
            )
            rewardDao.insertRewards(defaultRewards)
        }
    }
}
