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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PointTransactionEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ActivityHistoryScreen(
    transactions: List<PointTransactionEntity>,
    onBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredTransactions = remember(transactions, selectedFilter) {
        when (selectedFilter) {
            "EARNED" -> transactions.filter { it.type == "EARNED" }
            "SPENT" -> transactions.filter { it.type == "SPENT" }
            else -> transactions
        }
    }

    val dateFormat = remember { SimpleDateFormat("hh:mm a  dd MMMM yyyy", Locale("ar")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavyDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(NavyCard)
                    .testTag("history_back_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward, // RTL Forward is Back
                    contentDescription = "رجوع",
                    tint = GoldPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "سجل النشاط والعمليات",
                    color = GoldLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "تتبع دقيق لجميع نقاط GANI COINS المكتسبة والمصروفة",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "ALL" to "الكل (${transactions.size})",
                "EARNED" to "المكتسبة (${transactions.count { it.type == "EARNED" }})",
                "SPENT" to "المصروفة (${transactions.count { it.type == "SPENT" }})"
            ).forEach { (type, label) ->
                val isSelected = selectedFilter == type
                Surface(
                    onClick = { selectedFilter = type },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) GoldPrimary else NavyCard,
                    border = BorderStroke(1.dp, if (isSelected) GoldPrimary else NavyBorder),
                    modifier = Modifier.weight(1f)
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

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "لا توجد معاملات مسجلة في هذا القسم",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTransactions, key = { it.id }) { tx ->
                    val isEarned = tx.type == "EARNED"
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        border = BorderStroke(1.dp, NavyBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
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
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (isEarned) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isEarned) Icons.Filled.AddCircle else Icons.Filled.RemoveCircle,
                                        contentDescription = null,
                                        tint = if (isEarned) SuccessGreen else ErrorRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = tx.reason,
                                        color = TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = dateFormat.format(Date(tx.timestamp)),
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (isEarned) "+${tx.amount}" else "-${tx.amount}",
                                color = if (isEarned) SuccessGreen else ErrorRed,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
