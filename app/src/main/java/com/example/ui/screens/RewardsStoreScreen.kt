package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.local.entity.RewardItemEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.GoldButton
import com.example.ui.theme.*

@Composable
fun RewardsStoreScreen(
    user: UserEntity,
    rewards: List<RewardItemEntity>,
    ownedRewardIds: Set<String>,
    onPurchaseReward: (RewardItemEntity) -> Unit,
    onEquipReward: (RewardItemEntity) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredRewards = remember(rewards, selectedFilter) {
        when (selectedFilter) {
            "FRAME" -> rewards.filter { it.type == "FRAME" }
            "TITLE" -> rewards.filter { it.type == "TITLE" }
            "THEME" -> rewards.filter { it.type == "THEME" }
            else -> rewards
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavyDark)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Store Header Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.4f)),
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
                        text = "متجر المزايا الرقمية",
                        color = GoldLight,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "استبدل نقاطك الافتراضية بإطارات وألقاب فخمة لحسابك",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavyCard,
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.Stars, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        Text(text = "${user.ganiCoins}", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                "ALL" to "الكل",
                "FRAME" to "الإطارات",
                "TITLE" to "الألقاب",
                "THEME" to "السمات"
            )

            filters.forEach { (type, label) ->
                val isSelected = selectedFilter == type
                Surface(
                    onClick = { selectedFilter = type },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) GoldPrimary else NavyCard,
                    border = BorderStroke(1.dp, if (isSelected) GoldPrimary else NavyBorder),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("filter_$type")
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) DeepNavyDark else TextWhite,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Rewards Items Grid/List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredRewards, key = { it.id }) { reward ->
                val isOwned = ownedRewardIds.contains(reward.id)
                val isEquipped = when (reward.type) {
                    "FRAME" -> user.avatarFrame == reward.valueKey
                    "TITLE" -> user.customTitle == reward.title
                    "THEME" -> user.themeAccent == reward.valueKey
                    else -> false
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, if (isEquipped) GoldPrimary else NavyBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reward_item_${reward.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(NavyElevated)
                                    .border(1.dp, if (isEquipped) GoldPrimary else NavyBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (reward.type) {
                                        "FRAME" -> Icons.Filled.MilitaryTech
                                        "TITLE" -> Icons.Filled.EmojiEvents
                                        "THEME" -> Icons.Filled.Palette
                                        else -> Icons.Filled.Stars
                                    },
                                    contentDescription = null,
                                    tint = if (isEquipped) GoldPrimary else GoldLight,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = reward.title,
                                    color = if (isEquipped) GoldPrimary else TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = reward.description,
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Filled.Stars, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(12.dp))
                                    Text(
                                        text = "${reward.priceCoins} GANI COINS",
                                        color = GoldLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Action Button
                        when {
                            isEquipped -> {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = GoldPrimary.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, GoldPrimary)
                                ) {
                                    Text(
                                        text = "مفعّل الآن",
                                        color = GoldLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            isOwned -> {
                                OutlinedButton(
                                    onClick = { onEquipReward(reward) },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, GoldPrimary),
                                    modifier = Modifier.testTag("equip_reward_${reward.id}")
                                ) {
                                    Text("تفعيل", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            else -> {
                                GoldButton(
                                    text = "شراء",
                                    onClick = { onPurchaseReward(reward) },
                                    enabled = user.ganiCoins >= reward.priceCoins,
                                    modifier = Modifier.testTag("buy_reward_${reward.id}")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
