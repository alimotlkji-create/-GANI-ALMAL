package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.ChallengeEntity
import com.example.ui.theme.*
import com.example.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaniTopBar(
    title: String,
    coins: Long,
    onHistoryClick: (() -> Unit)? = null
) {
    Surface(
        color = DeepNavyDark,
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Title & Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(GoldLight, GoldSecondary, GoldDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.MonetizationOn,
                        contentDescription = "شعار جني المال",
                        tint = DeepNavyDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "GANI ALMAL",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Right side: Coins Pill Badge and Optional Admin Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Virtual Coins Counter Badge
                Surface(
                    onClick = { onHistoryClick?.invoke() },
                    shape = RoundedCornerShape(20.dp),
                    color = NavyCard,
                    border = BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("coins_counter_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Stars,
                            contentDescription = "عملات افتراضية",
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "$coins",
                            color = GoldLight,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "نقاط",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GaniBottomNavigation(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    NavigationBar(
        containerColor = DeepNavyDark,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(0.5.dp, NavyBorder),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        val tabs = listOf(
            Triple(MainTab.HOME, "الرئيسية", Icons.Filled.Home to Icons.Outlined.Home),
            Triple(MainTab.CHALLENGES, "التحديات", Icons.Filled.EmojiEvents to Icons.Outlined.EmojiEvents),
            Triple(MainTab.ADS, "الإعلانات", Icons.Filled.PlayCircle to Icons.Outlined.PlayCircleOutline),
            Triple(MainTab.REWARDS, "المكافآت", Icons.Filled.ShoppingBag to Icons.Outlined.ShoppingBag),
            Triple(MainTab.PROFILE, "حسابي", Icons.Filled.Person to Icons.Outlined.PersonOutline)
        )

        tabs.forEach { (tab, label, icons) ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) icons.first else icons.second,
                        contentDescription = label,
                        tint = if (isSelected) GoldPrimary else TextMuted
                    )
                },
                label = {
                    Text(
                        text = label,
                        color = if (isSelected) GoldPrimary else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = NavyElevated,
                    selectedIconColor = GoldPrimary,
                    unselectedIconColor = TextMuted,
                    selectedTextColor = GoldPrimary,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}

@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    testTag: String = "gold_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GoldPrimary,
            contentColor = DeepNavyDark,
            disabledContainerColor = NavyBorder,
            disabledContentColor = TextMuted
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        ),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GaniLevelBadge(level: String, modifier: Modifier = Modifier) {
    val (badgeColor, icon) = when (level) {
        "أسطورة" -> Pair(GoldPrimary, Icons.Filled.Stars)
        "خبير" -> Pair(GoldSecondary, Icons.Filled.WorkspacePremium)
        "محترف" -> Pair(Color(0xFFE0A96D), Icons.Filled.MilitaryTech)
        "متقدم" -> Pair(Color(0xFF70A1FF), Icons.Filled.Shield)
        "نشيط" -> Pair(Color(0xFF2ED573), Icons.Filled.Bolt)
        else -> Pair(TextMuted, Icons.Filled.StarBorder)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = NavyElevated,
        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = level,
                tint = badgeColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = level,
                color = badgeColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ChallengeActionDialog(
    challenge: ChallengeEntity,
    onDismiss: () -> Unit,
    onComplete: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(NavyElevated)
                        .border(1.dp, GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (challenge.category) {
                            "QUIZ" -> Icons.Filled.Quiz
                            "EDUCATIONAL" -> Icons.Filled.MenuBook
                            "DAILY_LOGIN" -> Icons.Filled.CalendarMonth
                            "EXPLORE" -> Icons.Filled.Explore
                            else -> Icons.Filled.EmojiEvents
                        },
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = challenge.title,
                    color = GoldLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = challenge.description,
                    color = TextMuted,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Points reward pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavyCard,
                    border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.4f)),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MonetizationOn,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "المكافأة: +${challenge.coinsReward} GANI COINS",
                            color = GoldPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Content depending on category
                when (challenge.category) {
                    "QUIZ" -> {
                        QuizInteractiveBody(
                            question = challenge.quizQuestion ?: "سؤال التحدي",
                            optionsString = challenge.quizOptions ?: "نعم|لا",
                            correctIndex = challenge.correctOptionIndex ?: 0,
                            onAnswered = { isCorrect -> onComplete(isCorrect) }
                        )
                    }
                    "EDUCATIONAL" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NavyCard)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "💡 نصيحة مالية ذكية:",
                                color = GoldPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = challenge.educationalFact ?: "الادخار والتخطيط المالي السليم هما أساس بناء الأمان المالي المستدام.",
                                color = TextWhite,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        GoldButton(
                            text = "قرأت واستوعبت المعلومة",
                            onClick = { onComplete(true) },
                            icon = Icons.Filled.CheckCircle,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "confirm_educational_button"
                        )
                    }
                    else -> {
                        // General or Login confirmation
                        Text(
                            text = "اضغط للتحقق من إتمام المهمة وتحديث رصيد نقاطك الافتراضية فوراً.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        GoldButton(
                            text = "تأكيد واستلام النقاط",
                            onClick = { onComplete(true) },
                            icon = Icons.Filled.Check,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "confirm_claim_button"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("dismiss_dialog_button")
                ) {
                    Text("إلغاء والعودة", color = TextMuted, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun QuizInteractiveBody(
    question: String,
    optionsString: String,
    correctIndex: Int,
    onAnswered: (Boolean) -> Unit
) {
    val options = remember(optionsString) { optionsString.split("|") }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = NavyCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = question,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        options.forEachIndexed { index, option ->
            val isCurrentSelected = selectedIndex == index
            val borderColor = when {
                isSubmitted && index == correctIndex -> SuccessGreen
                isSubmitted && isCurrentSelected && index != correctIndex -> ErrorRed
                isCurrentSelected -> GoldPrimary
                else -> NavyBorder
            }

            Surface(
                onClick = {
                    if (!isSubmitted) {
                        selectedIndex = index
                    }
                },
                shape = RoundedCornerShape(10.dp),
                color = if (isCurrentSelected) NavyElevated else NavyCard,
                border = BorderStroke(1.5.dp, borderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("quiz_option_$index")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = option,
                        color = if (isCurrentSelected) GoldLight else TextWhite,
                        fontSize = 13.sp,
                        fontWeight = if (isCurrentSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    if (isSubmitted) {
                        if (index == correctIndex) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen)
                        } else if (isCurrentSelected) {
                            Icon(Icons.Filled.Cancel, contentDescription = null, tint = ErrorRed)
                        }
                    }
                }
            }
        }

        if (feedbackMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = feedbackMessage!!,
                color = if (selectedIndex == correctIndex) SuccessGreen else ErrorRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GoldButton(
            text = if (!isSubmitted) "تحقق من الإجابة" else "متابعة واستلام المكافأة",
            onClick = {
                if (!isSubmitted) {
                    if (selectedIndex == null) {
                        feedbackMessage = "يرجى اختيار إجابة أولاً"
                    } else {
                        isSubmitted = true
                        val isCorrect = selectedIndex == correctIndex
                        feedbackMessage = if (isCorrect) "إجابة صحيحة وممتازة! 🎉" else "إجابة خاطئة! حظاً أوفر في المرة القادمة."
                    }
                } else {
                    onAnswered(selectedIndex == correctIndex)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            testTag = "submit_quiz_button"
        )
    }
}

@Composable
fun AdPlayerDialog(
    secondsRemaining: Int,
    onClaimReward: () -> Unit,
    onCloseEarly: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavyDark),
            border = BorderStroke(1.5.dp, GoldPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldSecondary.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, GoldSecondary)
                    ) {
                        Text(
                            text = "إعلان تجريبي معتمد",
                            color = GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(onClick = onCloseEarly) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "إغلاق الإعلان",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ad Graphic Screen Simulation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(NavyCard, NavyElevated, DeepNavyDark)
                            )
                        )
                        .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayCircle,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Google AdMob Rewarded Video",
                            color = GoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "بيئة إعلانات تجريبية آمنة متوافقة بالكامل مع سياسات Google",
                            color = TextMuted,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (secondsRemaining > 0) {
                    Text(
                        text = "يمكنك استلام المكافأة بعد: $secondsRemaining ثوانٍ",
                        color = GoldLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (5 - secondsRemaining) / 5f },
                        modifier = Modifier.fillMaxWidth(),
                        color = GoldPrimary,
                        trackColor = NavyBorder
                    )
                } else {
                    Text(
                        text = "اكتملت المشاهدة بنجاح! جاهز لاستلام المكافأة",
                        color = SuccessGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    GoldButton(
                        text = "استلام +50 GANI COINS",
                        onClick = onClaimReward,
                        icon = Icons.Filled.Stars,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "claim_ad_coins_button"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ملاحظة: النقاط الممنوحة هي نقاط افتراضية داخل التطبيق فقط وليست نقوداً حقيقية.",
                    color = TextMuted,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun LevelUpCelebrationDialog(
    newLevel: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = BorderStroke(2.dp, GoldPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(GoldLight, GoldPrimary, GoldDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.MilitaryTech,
                        contentDescription = null,
                        tint = DeepNavyDark,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ارتقاء في المستوى! 🎉",
                    color = GoldLight,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "أصبحت الآن في مستوى:",
                    color = TextMuted,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = newLevel,
                    color = GoldPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "واصل إكمال التحديات وجمع النقاط لفتح أرفع الرتب في جني المال!",
                    color = TextWhite,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                GoldButton(
                    text = "رائع، استمر!",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "dismiss_levelup_button"
                )
            }
        }
    }
}
