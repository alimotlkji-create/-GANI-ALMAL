package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.*
import com.example.data.repository.GaniRepository
import com.example.ui.components.GaniLevelBadge
import com.example.ui.components.GoldButton
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    user: UserEntity,
    recentTransactions: List<PointTransactionEntity>,
    rewards: List<RewardItemEntity>,
    onDailyChallengeClick: () -> Unit,
    onWatchAdClick: () -> Unit,
    onStoreClick: () -> Unit,
    onViewAllHistory: () -> Unit
) {
    val (levelName, levelFraction) = remember(user.levelXp) {
        GaniRepository.calculateLevelInfo(user.levelXp)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = levelFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )
    val nextLevelTarget = remember(user.levelXp) {
        GaniRepository.getNextLevelTarget(user.levelXp)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Welcome and User Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "أهلاً بك مجدداً،",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                        Text(
                            text = user.username,
                            color = GoldLight,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NavyElevated,
                            border = BorderStroke(0.5.dp, GoldSecondary.copy(alpha = 0.5f)),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = user.customTitle,
                                color = GoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Avatar with frame
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(64.dp)
                    ) {
                        // Frame border effect
                        val frameColor = when (user.avatarFrame) {
                            "gold_halo" -> GoldPrimary
                            "diamond_crown" -> Color(0xFF64B5F6)
                            "royal_shield" -> Color(0xFFFFB74D)
                            "neon_aura" -> Color(0xFF00E5FF)
                            else -> NavyBorder
                        }
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, frameColor, CircleShape)
                                .background(NavyCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (user.avatarIcon) {
                                    "avatar_2" -> Icons.Filled.MilitaryTech
                                    "avatar_3" -> Icons.Filled.WorkspacePremium
                                    "avatar_4" -> Icons.Filled.EmojiEvents
                                    else -> Icons.Filled.Person
                                },
                                contentDescription = "الصورة الرمزية",
                                tint = GoldLight,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }
            }
        }

        // Hero Banner Illustration
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                border = BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.gani_hero_banner),
                        contentDescription = "صندوق الجني الذهبي",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        DeepNavyDark.copy(alpha = 0.9f),
                                        DeepNavyDark.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "اجمع GANI COINS يومياً",
                            color = GoldLight,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "تحديات ثقافية وتعليمية بمكافآت حصرية",
                            color = TextWhite,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Virtual Coins Balance Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                border = BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_coins_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(NavyElevated, NavyCard)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(GoldLight, GoldPrimary, GoldDark)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MonetizationOn,
                                    contentDescription = null,
                                    tint = DeepNavyDark,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "رصيد النقاط الافتراضية",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "GANI COINS",
                                    color = GoldPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        GaniLevelBadge(level = user.level)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${user.ganiCoins}",
                        color = GoldLight,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "عملة رقمية غير نقدية تُستخدم حصرياً داخل التطبيق لفتح المزايا",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = NavyBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Level XP Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المستوى الحالي: ${user.level}",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${user.levelXp} / $nextLevelTarget XP",
                            color = GoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = GoldPrimary,
                        trackColor = NavyBorder
                    )
                }
            }
        }

        // Quick Action Buttons (Daily Challenge & Watch Ad)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Daily Challenge Card
                Card(
                    onClick = onDailyChallengeClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_daily_challenge_button")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "التحدي اليومي",
                            color = GoldLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "سؤال ثقافي واختبار",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Watch Optional Ad Card
                Card(
                    onClick = onWatchAdClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_watch_ad_button")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayCircle,
                            contentDescription = null,
                            tint = GoldSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "مشاهدة إعلان اختياري",
                            color = GoldLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+50 GANI COINS",
                            color = GoldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Stats Row (Completed challenges, Ads watched)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NavyCard,
                    border = BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen)
                        Column {
                            Text(text = "${user.completedChallengesCount}", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "تحديات مكتملة", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NavyCard,
                    border = BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Filled.Tv, contentDescription = null, tint = GoldSecondary)
                        Column {
                            Text(text = "${user.adsWatchedCount}", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "إعلانات تمت مشاهدتها", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Digital Rewards Store Spotlight
        if (rewards.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مكافآت رقمية حصرية",
                        color = GoldLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onStoreClick) {
                        Text("فتح المتجر", color = GoldPrimary, fontSize = 12.sp)
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(rewards.take(4)) { reward ->
                        Card(
                            onClick = onStoreClick,
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard),
                            border = BorderStroke(1.dp, NavyBorder),
                            modifier = Modifier.width(160.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(
                                    imageVector = when (reward.type) {
                                        "FRAME" -> Icons.Filled.MilitaryTech
                                        "TITLE" -> Icons.Filled.EmojiEvents
                                        "THEME" -> Icons.Filled.Palette
                                        else -> Icons.Filled.Stars
                                    },
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = reward.title,
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${reward.priceCoins} نقاط",
                                    color = GoldLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Activity Stream
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "آخر النشاطات",
                    color = GoldLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAllHistory) {
                    Text("عرض الكل", color = GoldPrimary, fontSize = 12.sp)
                }
            }

            if (recentTransactions.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "لا توجد نشاطات مسجلة بعد. أنجز تحدياً أو شاهد إعلاناً للبدء!",
                        color = TextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            } else {
                val dateFormat = remember { SimpleDateFormat("hh:mm a  dd/MM", Locale("ar")) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recentTransactions.take(4).forEach { tx ->
                        val isEarned = tx.type == "EARNED"
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = NavySurface),
                            border = BorderStroke(0.5.dp, NavyBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(if (isEarned) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isEarned) Icons.Filled.AddCircle else Icons.Filled.RemoveCircle,
                                            contentDescription = null,
                                            tint = if (isEarned) SuccessGreen else ErrorRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = tx.reason,
                                            color = TextWhite,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = dateFormat.format(Date(tx.timestamp)),
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Text(
                                    text = if (isEarned) "+${tx.amount}" else "-${tx.amount}",
                                    color = if (isEarned) SuccessGreen else ErrorRed,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
