package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdMobManager(private val context: Context) {
    companion object {
        private const val TAG = "AdMobManager"
        // Official Google AdMob Test Ad Units
        const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
        const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
        const val REWARD_COINS_AMOUNT = 50L
    }

    private var rewardedAd: RewardedAd? = null
    private var isLoading = false
    private var isInitialized = false

    fun initialize(onInitialized: () -> Unit = {}) {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) { status ->
                isInitialized = true
                Log.d(TAG, "MobileAds initialized: $status")
                loadRewardedAd()
                onInitialized()
            }
        } catch (t: Throwable) {
            Log.w(TAG, "Notice during MobileAds init (handled): ${t.message}")
        }
    }

    fun isAdReady(): Boolean = rewardedAd != null

    fun loadRewardedAd(onLoaded: ((Boolean) -> Unit)? = null) {
        if (isLoading || rewardedAd != null) return
        isLoading = true

        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                TEST_REWARDED_AD_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        isLoading = false
                        Log.d(TAG, "RewardedAd successfully loaded")
                        onLoaded?.invoke(true)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        rewardedAd = null
                        isLoading = false
                        Log.w(TAG, "RewardedAd failed to load: ${error.message}")
                        onLoaded?.invoke(false)
                    }
                }
            )
        } catch (t: Throwable) {
            rewardedAd = null
            isLoading = false
            Log.w(TAG, "Notice during loadRewardedAd (handled): ${t.message}")
            onLoaded?.invoke(false)
        }
    }

    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: (Long) -> Unit,
        onAdClosed: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentAd = rewardedAd
        if (currentAd == null) {
            onError("الإعلان غير جاهز حالياً، جاري محاولة التحميل...")
            loadRewardedAd()
            return
        }

        var rewardAwarded = false
        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                loadRewardedAd() // Pre-load next
                onAdClosed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                rewardedAd = null
                loadRewardedAd()
                onError("فشل عرض الإعلان: ${error.message}")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed full screen")
            }
        }

        currentAd.show(activity) { rewardItem: RewardItem ->
            if (!rewardAwarded) {
                rewardAwarded = true
                val earned = if (rewardItem.amount > 0) rewardItem.amount.toLong() else REWARD_COINS_AMOUNT
                onRewardEarned(earned)
            }
        }
    }
}
