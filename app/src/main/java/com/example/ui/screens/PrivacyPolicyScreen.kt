package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
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
                    .testTag("privacy_back_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "رجوع",
                    tint = GoldPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "سياسة الخصوصية وشروط الاستخدام",
                    color = GoldLight,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "تطبيق جني المال GANI ALMAL",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Core Disclaimer Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.5.dp, GoldPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Gavel, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                            Text(
                                text = "1. طبيعة النقاط الافتراضية (GANI COINS):",
                                color = GoldLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "جميع النقاط الممنوحة في التطبيق (GANI COINS) هي نقاط افتراضية وتفاعلية رقمية داخلية فقط. لا تمثل أموالاً حقيقية، ولا تعادل أي عملة نقدية، ولا يمكن سحبها إلى أي محفظة أو حساب بنكي، ولا يمكن تحويلها بين المستخدمين، ولا يمكن استبدالها ببطاقات هدايا أو عملات مشفرة. تُستخدم هذه النقاط فقط لفتح الألقاب والإطارات والمزايا التنافسية داخل التطبيق.",
                            color = TextWhite,
                            fontSize = 12.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            // Data & Privacy Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Security, contentDescription = null, tint = InfoBlue, modifier = Modifier.size(24.dp))
                            Text(
                                text = "2. حماية وخصوصية البيانات:",
                                color = GoldLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "• كلمات المرور مشفرة ومحمية بدوال تجزئة قوية (SHA-256 مع Salt) ولا يتم تخزينها كنص عادي إطلاقاً.\n" +
                                    "• يتم الاحتفاظ ببيانات النشاط والحساب على جهازك وقواعد البيانات المشفرة لضمان استمرارية نقاطك وإنجازاتك.\n" +
                                    "• يحق للمستخدم في أي وقت حذف حسابه وجميع بياناته وسجل نشاطاته بصورة نهائية من داخل صفحة الملف الشخصي.",
                            color = TextWhite.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            // Ads Policy Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "3. سياسة الإعلانات والامتثال لقوانين Google:",
                            color = GoldLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "• التطبيق يستخدم حزمة Google Mobile Ads الرسمية وفق إرشادات المطورين لشركة Google.\n" +
                                    "• جميع إعلانات المكافآت هي إعلانات اختيارية بالكامل ولا توجد إعلانات إجبارية لاستخدام الخدمات الأساسية.\n" +
                                    "• يمنع التطبيق أي ممارسات مضللة أو نقرات خادعة أو وعود بمكاسب مالية نقدية.",
                            color = TextWhite.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }
    }
}
