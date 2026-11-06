package com.example.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdMobManager
import com.example.data.local.AppDatabase
import com.example.data.local.entity.*
import com.example.data.repository.GaniRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen {
    SPLASH,
    AUTH,
    MAIN,
    PRIVACY,
    TERMS,
    HISTORY
}

enum class MainTab {
    HOME,
    CHALLENGES,
    ADS,
    REWARDS,
    PROFILE
}

class GaniViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    val repository = GaniRepository(database)
    val adMobManager = AdMobManager(application)

    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.observeUser(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val challenges: StateFlow<List<ChallengeEntity>> = repository.observeChallenges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProgressMap: StateFlow<Map<String, UserChallengeProgressEntity>> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.observeUserProgress(id)
    }.map { list ->
        list.associateBy { it.challengeId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val rewards: StateFlow<List<RewardItemEntity>> = repository.observeRewards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userOwnedRewardIds: StateFlow<Set<String>> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.observeUserOwnedRewards(id)
    }.map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val transactions: StateFlow<List<PointTransactionEntity>> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.observeTransactions(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin flows
    val allUsers: StateFlow<List<UserEntity>> = repository.observeAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<ReportEntity>> = repository.observeAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _adminStats = MutableStateFlow<Map<String, Any>>(emptyMap())
    val adminStats: StateFlow<Map<String, Any>> = _adminStats.asStateFlow()

    // UI state feedback
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _levelUpCelebration = MutableStateFlow<String?>(null)
    val levelUpCelebration: StateFlow<String?> = _levelUpCelebration.asStateFlow()

    private val _activeChallengeDialog = MutableStateFlow<ChallengeEntity?>(null)
    val activeChallengeDialog: StateFlow<ChallengeEntity?> = _activeChallengeDialog.asStateFlow()

    // Simulated Ad Player State (when AdMob test device/network is loading or simulator is used)
    private val _isShowingAdPlayer = MutableStateFlow(false)
    val isShowingAdPlayer: StateFlow<Boolean> = _isShowingAdPlayer.asStateFlow()

    private val _adTimerSeconds = MutableStateFlow(5)
    val adTimerSeconds: StateFlow<Int> = _adTimerSeconds.asStateFlow()

    private var adCountdownJob: Job? = null

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
            adMobManager.initialize()
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun showMessage(msg: String) {
        _uiMessage.value = msg
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    fun dismissLevelUp() {
        _levelUpCelebration.value = null
    }

    fun openChallengeDialog(challenge: ChallengeEntity) {
        _activeChallengeDialog.value = challenge
    }

    fun dismissChallengeDialog() {
        _activeChallengeDialog.value = null
    }

    // --- Auth Actions ---

    fun register(username: String, email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.registerUser(username, email, pass)
            result.onSuccess { user ->
                _currentUserId.value = user.id
                _currentScreen.value = AppScreen.MAIN
                _currentTab.value = MainTab.HOME
                _uiMessage.value = "مرحباً بك يا ${user.username}! تم منحك 100 GANI COINS هدية ترحيبية"
                onSuccess()
            }.onFailure { err ->
                _uiMessage.value = err.message ?: "فشل إنشاء الحساب"
            }
        }
    }

    fun login(identifier: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.loginUser(identifier, pass)
            result.onSuccess { user ->
                _currentUserId.value = user.id
                _currentScreen.value = AppScreen.MAIN
                _currentTab.value = MainTab.HOME
                _uiMessage.value = "أهلاً بك مجدداً يا ${user.username}"
                onSuccess()
            }.onFailure { err ->
                _uiMessage.value = err.message ?: "فشل تسجيل الدخول"
            }
        }
    }

    fun resetPassword(identifier: String, newPass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.resetPassword(identifier, newPass)
            result.onSuccess {
                _uiMessage.value = "تم تحديث كلمة المرور بنجاح، يمكنك تسجيل الدخول الآن"
                onSuccess()
            }.onFailure { err ->
                _uiMessage.value = err.message ?: "فشل إعادة تعيين كلمة المرور"
            }
        }
    }

    fun logout() {
        _currentUserId.value = null
        _currentScreen.value = AppScreen.AUTH
        _uiMessage.value = "تم تسجيل الخروج بنجاح"
    }

    fun deleteAccount() {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            repository.deleteUserAccount(uid)
            _currentUserId.value = null
            _currentScreen.value = AppScreen.AUTH
            _uiMessage.value = "تم حذف الحساب وجميع البيانات المرتبطة به نهائياً"
        }
    }

    // --- Challenge Completion ---

    fun completeChallengeWithVerification(challenge: ChallengeEntity, isVerified: Boolean) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            val result = repository.completeAndClaimChallenge(uid, challenge.id, isVerified)
            result.onSuccess { (coins, leveledUp) ->
                _uiMessage.value = "تهانينا! حصلت على +$coins من GANI COINS"
                if (leveledUp) {
                    val user = repository.getUser(uid)
                    _levelUpCelebration.value = user?.level ?: "مستوى جديد"
                }
                _activeChallengeDialog.value = null
            }.onFailure { err ->
                _uiMessage.value = err.message ?: "تعذر استلام النقاط"
            }
        }
    }

    // --- Ad Reward System ---

    fun requestWatchAd(activity: Activity?) {
        val uid = _currentUserId.value ?: return

        if (activity != null && adMobManager.isAdReady()) {
            adMobManager.showRewardedAd(
                activity = activity,
                onRewardEarned = { amount ->
                    onAdFinishedSuccessfully(uid, amount)
                },
                onAdClosed = {
                    // pre-load next ad
                    adMobManager.loadRewardedAd()
                },
                onError = { err ->
                    // fallback to interactive ad verification modal
                    startInteractiveAdSimulation(uid)
                }
            )
        } else {
            // Interactive in-app compliant ad experience
            startInteractiveAdSimulation(uid)
        }
    }

    private fun startInteractiveAdSimulation(userId: String) {
        _adTimerSeconds.value = 5
        _isShowingAdPlayer.value = true
        adCountdownJob?.cancel()
        adCountdownJob = viewModelScope.launch {
            for (i in 5 downTo 1) {
                _adTimerSeconds.value = i
                delay(1000)
            }
            _adTimerSeconds.value = 0
        }
    }

    fun claimSimulatedAdReward() {
        val uid = _currentUserId.value ?: return
        if (_adTimerSeconds.value == 0) {
            _isShowingAdPlayer.value = false
            onAdFinishedSuccessfully(uid, AdMobManager.REWARD_COINS_AMOUNT)
        } else {
            _uiMessage.value = "يرجى الانتظار حتى اكتمال مدة الإعلان لاستلام المكافأة"
        }
    }

    fun closeAdPlayerEarly() {
        adCountdownJob?.cancel()
        _isShowingAdPlayer.value = false
        _uiMessage.value = "تم إغلاق الإعلان قبل نهايته، لم يتم احتساب النقاط"
    }

    private fun onAdFinishedSuccessfully(userId: String, amount: Long) {
        viewModelScope.launch {
            val result = repository.recordAndAwardAd(userId, AdMobManager.TEST_REWARDED_AD_UNIT_ID, amount)
            result.onSuccess { earned ->
                _uiMessage.value = "رائع! تمت مشاهدة الإعلان بنجاح وحصلت على +$earned GANI COINS"
            }.onFailure { err ->
                _uiMessage.value = err.message ?: "تعذر احتساب مكافأة الإعلان"
            }
        }
    }

    // --- Store & Rewards ---

    fun purchaseReward(reward: RewardItemEntity) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            val result = repository.purchaseReward(uid, reward.id)
            result.onSuccess {
                _uiMessage.value = "تم شراء '${reward.title}' وتفعيلها بنجاح في حسابك!"
            }.onFailure { err ->
                _uiMessage.value = err.message ?: "فشلت عملية الشراء"
            }
        }
    }

    fun equipReward(reward: RewardItemEntity) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            val result = repository.equipReward(uid, reward)
            result.onSuccess {
                _uiMessage.value = "تم تفعيل '${reward.title}' على ملفك الشخصي"
            }.onFailure { err ->
                _uiMessage.value = err.message ?: "فشل التفعيل"
            }
        }
    }

    fun updateAvatarIcon(icon: String) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            repository.updateAvatarIcon(uid, icon)
            _uiMessage.value = "تم تحديث الصورة الرمزية بنجاح"
        }
    }

    // --- Admin Functions ---

    fun refreshAdminStats() {
        viewModelScope.launch {
            _adminStats.value = repository.getAdminStats()
        }
    }

    fun addCustomChallenge(challenge: ChallengeEntity) {
        viewModelScope.launch {
            repository.addCustomChallenge(challenge)
            _uiMessage.value = "تمت إضافة التحدي الجديد بنجاح"
        }
    }

    fun deleteChallenge(challengeId: String) {
        viewModelScope.launch {
            repository.deleteChallenge(challengeId)
            _uiMessage.value = "تم حذف التحدي"
        }
    }

    fun addCustomReward(reward: RewardItemEntity) {
        viewModelScope.launch {
            repository.addCustomReward(reward)
            _uiMessage.value = "تمت إضافة المكافأة الجديدة إلى المتجر"
        }
    }

    fun toggleUserSuspension(user: UserEntity) {
        viewModelScope.launch {
            val newStatus = !user.isSuspended
            repository.setUserSuspended(user.id, newStatus)
            _uiMessage.value = if (newStatus) "تم إيقاف حساب ${user.username}" else "تم تفعيل حساب ${user.username}"
        }
    }

    fun sendReport(subject: String, message: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.submitReport(user.id, user.username, subject, message)
            _uiMessage.value = "شكراً لك، تم إرسال رسالتك للإدارة وسيتم مراجعتها"
        }
    }
}
