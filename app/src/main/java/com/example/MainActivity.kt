package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.*

class MainActivity : ComponentActivity() {
    private val viewModel: GaniViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GaniAlmalTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    GaniAlmalApp(
                        viewModel = viewModel,
                        activity = this
                    )
                }
            }
        }
    }
}

@Composable
fun GaniAlmalApp(
    viewModel: GaniViewModel,
    activity: ComponentActivity
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val challenges by viewModel.challenges.collectAsStateWithLifecycle()
    val userProgressMap by viewModel.userProgressMap.collectAsStateWithLifecycle()
    val rewards by viewModel.rewards.collectAsStateWithLifecycle()
    val ownedRewardIds by viewModel.userOwnedRewardIds.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val adminStats by viewModel.adminStats.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allReports by viewModel.allReports.collectAsStateWithLifecycle()

    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()
    val levelUpCelebration by viewModel.levelUpCelebration.collectAsStateWithLifecycle()
    val activeChallengeDialog by viewModel.activeChallengeDialog.collectAsStateWithLifecycle()
    val isShowingAdPlayer by viewModel.isShowingAdPlayer.collectAsStateWithLifecycle()
    val adTimerSeconds by viewModel.adTimerSeconds.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessage()
        }
    }

    // Handle Back Press on sub-screens
    if (currentScreen != AppScreen.MAIN && currentScreen != AppScreen.AUTH && currentScreen != AppScreen.SPLASH) {
        BackHandler {
            viewModel.navigateTo(AppScreen.MAIN)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavyDark)
    ) {
        when (currentScreen) {
            AppScreen.SPLASH -> {
                SplashScreen(
                    onTimeout = {
                        if (viewModel.currentUser.value != null) {
                            viewModel.navigateTo(AppScreen.MAIN)
                        } else {
                            viewModel.navigateTo(AppScreen.AUTH)
                        }
                    }
                )
            }

            AppScreen.AUTH -> {
                AuthScreen(
                    onLogin = { id, pass -> viewModel.login(id, pass) {} },
                    onRegister = { u, e, p -> viewModel.register(u, e, p) {} },
                    onResetPassword = { id, p -> viewModel.resetPassword(id, p) {} },
                    onViewPrivacy = { viewModel.navigateTo(AppScreen.PRIVACY) }
                )
            }

            AppScreen.MAIN -> {
                val user = currentUser
                if (user == null) {
                    // Safety check if session invalid
                    LaunchedEffect(Unit) {
                        viewModel.navigateTo(AppScreen.AUTH)
                    }
                } else {
                    Scaffold(
                        topBar = {
                            GaniTopBar(
                                title = when (currentTab) {
                                    MainTab.HOME -> "الرئيسية"
                                    MainTab.CHALLENGES -> "التحديات"
                                    MainTab.ADS -> "مركز الإعلانات"
                                    MainTab.REWARDS -> "متجر المكافآت"
                                    MainTab.PROFILE -> "الملف الشخصي"
                                },
                                coins = user.ganiCoins,
                                onHistoryClick = { viewModel.navigateTo(AppScreen.HISTORY) }
                            )
                        },
                        bottomBar = {
                            GaniBottomNavigation(
                                selectedTab = currentTab,
                                onTabSelected = { viewModel.selectTab(it) }
                            )
                        },
                        snackbarHost = {
                            SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier
                                    .padding(bottom = 70.dp)
                                    .navigationBarsPadding()
                            ) { data ->
                                Snackbar(
                                    snackbarData = data,
                                    containerColor = NavyElevated,
                                    contentColor = GoldLight,
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                                )
                            }
                        },
                        containerColor = DeepNavyDark,
                        contentWindowInsets = WindowInsets(0, 0, 0, 0)
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                label = "tab_transition"
                            ) { tab ->
                                when (tab) {
                                    MainTab.HOME -> {
                                        HomeScreen(
                                            user = user,
                                            recentTransactions = transactions,
                                            rewards = rewards,
                                            onDailyChallengeClick = { viewModel.selectTab(MainTab.CHALLENGES) },
                                            onWatchAdClick = { viewModel.requestWatchAd(activity) },
                                            onStoreClick = { viewModel.selectTab(MainTab.REWARDS) },
                                            onViewAllHistory = { viewModel.navigateTo(AppScreen.HISTORY) }
                                        )
                                    }
                                    MainTab.CHALLENGES -> {
                                        ChallengesScreen(
                                            challenges = challenges,
                                            userProgressMap = userProgressMap,
                                            onStartChallenge = { viewModel.openChallengeDialog(it) }
                                        )
                                    }
                                    MainTab.ADS -> {
                                        AdsScreen(
                                            adsWatchedCount = user.adsWatchedCount,
                                            onWatchRewardedAd = { viewModel.requestWatchAd(it) }
                                        )
                                    }
                                    MainTab.REWARDS -> {
                                        RewardsStoreScreen(
                                            user = user,
                                            rewards = rewards,
                                            ownedRewardIds = ownedRewardIds,
                                            onPurchaseReward = { viewModel.purchaseReward(it) },
                                            onEquipReward = { viewModel.equipReward(it) }
                                        )
                                    }
                                    MainTab.PROFILE -> {
                                        ProfileScreen(
                                            user = user,
                                            onNavigateToHistory = { viewModel.navigateTo(AppScreen.HISTORY) },
                                            onNavigateToPrivacy = { viewModel.navigateTo(AppScreen.PRIVACY) },
                                            onSelectAvatar = { viewModel.updateAvatarIcon(it) },
                                            onSendFeedback = { sub, msg -> viewModel.sendReport(sub, msg) },
                                            onLogout = { viewModel.logout() },
                                            onDeleteAccount = { viewModel.deleteAccount() }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }


            AppScreen.HISTORY -> {
                ActivityHistoryScreen(
                    transactions = transactions,
                    onBack = { viewModel.navigateTo(AppScreen.MAIN) }
                )
            }

            AppScreen.PRIVACY, AppScreen.TERMS -> {
                PrivacyPolicyScreen(
                    onBack = {
                        if (currentUser != null) {
                            viewModel.navigateTo(AppScreen.MAIN)
                        } else {
                            viewModel.navigateTo(AppScreen.AUTH)
                        }
                    }
                )
            }
        }

        // Active Interactive Challenge Dialog (Quiz / Educational / Login verification)
        activeChallengeDialog?.let { challenge ->
            ChallengeActionDialog(
                challenge = challenge,
                onDismiss = { viewModel.dismissChallengeDialog() },
                onComplete = { isVerified ->
                    viewModel.completeChallengeWithVerification(challenge, isVerified)
                }
            )
        }

        // Rewarded Ad Player Dialog
        if (isShowingAdPlayer) {
            AdPlayerDialog(
                secondsRemaining = adTimerSeconds,
                onClaimReward = { viewModel.claimSimulatedAdReward() },
                onCloseEarly = { viewModel.closeAdPlayerEarly() }
            )
        }

        // Level Up Celebration Popup
        levelUpCelebration?.let { newLevel ->
            LevelUpCelebrationDialog(
                newLevel = newLevel,
                onDismiss = { viewModel.dismissLevelUp() }
            )
        }

    }
}
