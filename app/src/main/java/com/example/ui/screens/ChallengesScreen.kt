package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ChallengeEntity
import com.example.data.local.entity.UserChallengeProgressEntity
import com.example.data.repository.GaniRepository
import com.example.ui.components.GoldButton
import com.example.ui.theme.*

@Composable
fun ChallengesScreen(
    challenges: List<ChallengeEntity>,
    userProgressMap: Map<String, UserChallengeProgressEntity>,
    onStartChallenge: (ChallengeEntity) -> Unit
) {
    var selectedTab by remember { mutableStateOf("DAILY") }
    val today = remember { GaniRepository.getTodayString() }

    val filteredChallenges = remember(challenges, selectedTab) {
        challenges.filter { it.type == selectedTab }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavyDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector (Daily vs Weekly)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = NavySurface,
            border = BorderStroke(1.dp, NavyBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                Surface(
                    onClick = { selectedTab = "DAILY" },
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedTab == "DAILY") GoldPrimary else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_daily_challenges")
                ) {
                    Text(
                        text = "التحديات اليومية",
                        color = if (selectedTab == "DAILY") DeepNavyDark else TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                Surface(
                    onClick = { selectedTab = "WEEKLY" },
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedTab == "WEEKLY") GoldPrimary else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_weekly_challenges")
                ) {
                    Text(
                        text = "التحديات الأسبوعية",
                        color = if (selectedTab == "WEEKLY") DeepNavyDark else TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Challenges List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredChallenges, key = { it.id }) { challenge ->
                val progress = userProgressMap[challenge.id]
                val isClaimedToday = progress?.isClaimed == true && (challenge.type == "WEEKLY" || progress.lastDate == today)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, if (isClaimedToday) SuccessGreen.copy(alpha = 0.5f) else NavyBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("challenge_card_${challenge.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (isClaimedToday) SuccessGreen.copy(alpha = 0.2f) else NavyElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (challenge.category) {
                                            "DAILY_LOGIN" -> Icons.Filled.CalendarToday
                                            "QUIZ" -> Icons.Filled.Psychology
                                            "EDUCATIONAL" -> Icons.Filled.School
                                            "EXPLORE" -> Icons.Filled.Explore
                                            "STREAK" -> Icons.Filled.Whatshot
                                            else -> Icons.Filled.EmojiEvents
                                        },
                                        contentDescription = null,
                                        tint = if (isClaimedToday) SuccessGreen else GoldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = challenge.title,
                                        color = if (isClaimedToday) SuccessGreen else GoldLight,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = NavyCard,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Text(
                                            text = when (challenge.category) {
                                                "DAILY_LOGIN" -> "تسجيل حضور"
                                                "QUIZ" -> "سؤال واختبار"
                                                "EDUCATIONAL" -> "معرفة مالية"
                                                "EXPLORE" -> "استكشاف"
                                                "STREAK" -> "سلسلة إنجاز"
                                                else -> "تحدي عام"
                                            },
                                            color = TextMuted,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            // Points Pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NavyCard,
                                border = BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Filled.Stars, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = "+${challenge.coinsReward}",
                                        color = GoldPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = challenge.description,
                            color = TextWhite.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Button
                        if (isClaimedToday) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SuccessGreen.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "تم إكمال التحدي واستلام النقاط بنجاح",
                                        color = SuccessGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            GoldButton(
                                text = "بدء التحدي",
                                onClick = { onStartChallenge(challenge) },
                                icon = Icons.Filled.PlayArrow,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "start_challenge_${challenge.id}"
                            )
                        }
                    }
                }
            }
        }
    }
}
