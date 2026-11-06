package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.UserEntity
import com.example.ui.components.GaniLevelBadge
import com.example.ui.components.GoldButton
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    user: UserEntity,
    onNavigateToHistory: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onSelectAvatar: (String) -> Unit,
    onSendFeedback: (String, String) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    var showAvatarDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavyDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        // User Profile Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar with active equipped frame
                    val frameColor = when (user.avatarFrame) {
                        "gold_halo" -> GoldPrimary
                        "diamond_crown" -> Color(0xFF64B5F6)
                        "royal_shield" -> Color(0xFFFFB74D)
                        "neon_aura" -> Color(0xFF00E5FF)
                        else -> NavyBorder
                    }

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(3.dp, frameColor, CircleShape)
                            .clickable { showAvatarDialog = true }
                            .testTag("avatar_profile_image"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(NavyElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (user.avatarIcon) {
                                    "avatar_2" -> Icons.Filled.MilitaryTech
                                    "avatar_3" -> Icons.Filled.WorkspacePremium
                                    "avatar_4" -> Icons.Filled.EmojiEvents
                                    else -> Icons.Filled.Person
                                },
                                contentDescription = null,
                                tint = GoldLight,
                                modifier = Modifier.size(46.dp)
                            )
                        }

                        // Edit icon badge
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(GoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "تغيير الصورة الرمزية",
                                tint = DeepNavyDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user.username,
                        color = GoldLight,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (user.email.isNotEmpty()) {
                        Text(
                            text = user.email,
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Title & Level Badges
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavyElevated,
                            border = BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = user.customTitle,
                                color = GoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        GaniLevelBadge(level = user.level)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Points Summary Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NavyCard)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${user.ganiCoins}", color = GoldLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "النقاط الافتراضية", color = TextMuted, fontSize = 11.sp)
                        }
                        VerticalDivider(modifier = Modifier.height(30.dp), color = NavyBorder)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${user.completedChallengesCount}", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "تحديات مكتملة", color = TextMuted, fontSize = 11.sp)
                        }
                        VerticalDivider(modifier = Modifier.height(30.dp), color = NavyBorder)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${user.adsWatchedCount}", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "إعلانات", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Account Options Menu
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ProfileMenuItem(
                        icon = Icons.Filled.History,
                        title = "سجل العمليات والنشاط",
                        subtitle = "عرض تفاصيل النقاط المكتسبة والمصروفة",
                        onClick = onNavigateToHistory,
                        testTag = "menu_history"
                    )

                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

                    ProfileMenuItem(
                        icon = Icons.Filled.Feedback,
                        title = "تواصل مع الإدارة / إرسال بلاغ",
                        subtitle = "اقتراحات، ملاحظات أو استفسارات",
                        onClick = { showFeedbackDialog = true },
                        testTag = "menu_feedback"
                    )

                    HorizontalDivider(color = NavyBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

                    ProfileMenuItem(
                        icon = Icons.Filled.Security,
                        title = "سياسة الخصوصية وشروط الاستخدام",
                        subtitle = "قواعد النقاط الافتراضية وحماية البيانات",
                        onClick = onNavigateToPrivacy,
                        testTag = "menu_privacy"
                    )
                }
            }
        }

        // Logout and Delete Account Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onLogout,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GoldSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("logout_button")
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = null, tint = GoldSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تسجيل الخروج", color = GoldSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("delete_account_button")
                ) {
                    Icon(Icons.Filled.DeleteForever, contentDescription = null, tint = ErrorRed)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حذف الحساب نهائياً", color = ErrorRed, fontSize = 13.sp)
                }
            }
        }
    }

    // Avatar Picker Dialog
    if (showAvatarDialog) {
        Dialog(onDismissRequest = { showAvatarDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, GoldPrimary),
                modifier = Modifier.padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("اختر الصورة الرمزية", color = GoldLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("avatar_1", "avatar_2", "avatar_3", "avatar_4").forEach { iconKey ->
                            val isSelected = user.avatarIcon == iconKey
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) GoldPrimary else NavyCard)
                                    .border(2.dp, if (isSelected) GoldLight else NavyBorder, CircleShape)
                                    .clickable {
                                        onSelectAvatar(iconKey)
                                        showAvatarDialog = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (iconKey) {
                                        "avatar_2" -> Icons.Filled.MilitaryTech
                                        "avatar_3" -> Icons.Filled.WorkspacePremium
                                        "avatar_4" -> Icons.Filled.EmojiEvents
                                        else -> Icons.Filled.Person
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) DeepNavyDark else GoldLight,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { showAvatarDialog = false }) {
                        Text("إغلاق", color = TextMuted)
                    }
                }
            }
        }
    }

    // Send Feedback / Report Dialog
    if (showFeedbackDialog) {
        var subject by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showFeedbackDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, GoldPrimary),
                modifier = Modifier.padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("إرسال رسالة للإدارة", color = GoldLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("الموضوع") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("نص الرسالة أو البلاغ") },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = NavyBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GoldButton(
                        text = "إرسال",
                        onClick = {
                            if (subject.isNotBlank() && message.isNotBlank()) {
                                onSendFeedback(subject, message)
                                showFeedbackDialog = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextButton(onClick = { showFeedbackDialog = false }) {
                        Text("إلغاء", color = TextMuted)
                    }
                }
            }
        }
    }

    // Delete Account Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("تأكيد حذف الحساب", color = ErrorRed, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "هل أنت متأكد من رغبتك في حذف الحساب نهائياً؟ سيتم مسح جميع النقاط الافتراضية، وسجل النشاطات، والمكافآت المشتراة ولا يمكن التراجع عن هذا الإجراء.",
                    color = TextWhite,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("نعم، احذف الحساب نهائياً", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("إلغاء", color = TextMuted)
                }
            },
            containerColor = NavySurface
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tint: Color = GoldLight,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Column {
                Text(text = title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
            }
        }
        Icon(Icons.Filled.ChevronLeft, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
    }
}
