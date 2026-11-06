package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GoldButton
import com.example.ui.theme.*

enum class AuthMode {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String) -> Unit,
    onResetPassword: (String, String) -> Unit,
    onViewPrivacy: () -> Unit
) {
    var authMode by remember { mutableStateOf(AuthMode.LOGIN) }
    var username by remember { mutableStateOf("") }
    var emailOrUsername by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepNavyDark, NavyBackground, NavySurface)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Emblem
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(GoldLight, GoldPrimary, GoldDark)
                        )
                    )
                    .border(2.dp, GoldLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MonetizationOn,
                    contentDescription = "جني المال",
                    tint = DeepNavyDark,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "جني المال",
                color = GoldPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = when (authMode) {
                    AuthMode.LOGIN -> "تسجيل الدخول إلى حسابك"
                    AuthMode.REGISTER -> "إنشاء حساب جديد وجمع النقاط"
                    AuthMode.FORGOT_PASSWORD -> "استعادة وتعيين كلمة المرور"
                },
                color = TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Auth Tab Switcher
            if (authMode != AuthMode.FORGOT_PASSWORD) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavyCard,
                    border = BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Surface(
                            onClick = { authMode = AuthMode.LOGIN },
                            shape = RoundedCornerShape(8.dp),
                            color = if (authMode == AuthMode.LOGIN) GoldPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_login")
                        ) {
                            Text(
                                text = "تسجيل الدخول",
                                color = if (authMode == AuthMode.LOGIN) DeepNavyDark else TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }

                        Surface(
                            onClick = { authMode = AuthMode.REGISTER },
                            shape = RoundedCornerShape(8.dp),
                            color = if (authMode == AuthMode.REGISTER) GoldPrimary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_register")
                        ) {
                            Text(
                                text = "حساب جديد (+100 هدية)",
                                color = if (authMode == AuthMode.REGISTER) DeepNavyDark else TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card Form Container
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (authMode) {
                        AuthMode.LOGIN -> {
                            OutlinedTextField(
                                value = emailOrUsername,
                                onValueChange = { emailOrUsername = it },
                                label = { Text("اسم المستخدم أو البريد الإلكتروني") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = GoldPrimary)
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedLabelColor = GoldPrimary,
                                    unfocusedLabelColor = TextMuted,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_identifier_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("كلمة المرور") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = null, tint = GoldPrimary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                            contentDescription = null,
                                            tint = TextMuted
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedLabelColor = GoldPrimary,
                                    unfocusedLabelColor = TextMuted,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { authMode = AuthMode.FORGOT_PASSWORD },
                                    modifier = Modifier.testTag("forgot_password_button")
                                ) {
                                    Text("نسيت كلمة المرور؟", color = GoldLight, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            GoldButton(
                                text = "دخول",
                                onClick = {
                                    if (emailOrUsername.isBlank() || password.isBlank()) {
                                        localError = "يرجى ملء جميع الحقول"
                                    } else {
                                        localError = null
                                        onLogin(emailOrUsername, password)
                                    }
                                },
                                icon = Icons.Filled.Login,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "submit_login_button"
                            )
                        }

                        AuthMode.REGISTER -> {
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text("اسم المستخدم (3 أحرف على الأقل)") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Badge, contentDescription = null, tint = GoldPrimary)
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedLabelColor = GoldPrimary,
                                    unfocusedLabelColor = TextMuted,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_username_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("البريد الإلكتروني") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Email, contentDescription = null, tint = GoldPrimary)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedLabelColor = GoldPrimary,
                                    unfocusedLabelColor = TextMuted,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_email_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("كلمة المرور (6 خانات على الأقل)") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = null, tint = GoldPrimary)
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedLabelColor = GoldPrimary,
                                    unfocusedLabelColor = TextMuted,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("register_password_input")
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Welcome gift pill indicator
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NavyElevated,
                                border = BorderStroke(1.dp, GoldSecondary.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.Stars, contentDescription = null, tint = GoldPrimary)
                                    Text(
                                        text = "هدية الانضمام: 100 GANI COINS تُضاف فوراً لحسابك!",
                                        color = GoldLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            GoldButton(
                                text = "إنشاء الحساب واستلام الهدية",
                                onClick = {
                                    if (username.isBlank() || password.isBlank()) {
                                        localError = "يرجى إدخال اسم المستخدم وكلمة المرور"
                                    } else {
                                        localError = null
                                        onRegister(username, email, password)
                                    }
                                },
                                icon = Icons.Filled.AppRegistration,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "submit_register_button"
                            )
                        }

                        AuthMode.FORGOT_PASSWORD -> {
                            Text(
                                text = "أدخل بريدك الإلكتروني أو اسم المستخدم وكلمة المرور الجديدة لإعادة التعيين:",
                                color = TextMuted,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = emailOrUsername,
                                onValueChange = { emailOrUsername = it },
                                label = { Text("اسم المستخدم أو البريد") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = GoldPrimary)
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedLabelColor = GoldPrimary,
                                    unfocusedLabelColor = TextMuted,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("كلمة المرور الجديدة") },
                                leadingIcon = {
                                    Icon(Icons.Filled.LockReset, contentDescription = null, tint = GoldPrimary)
                                },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = NavyBorder,
                                    focusedLabelColor = GoldPrimary,
                                    unfocusedLabelColor = TextMuted,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            GoldButton(
                                text = "تأكيد كلمة المرور الجديدة",
                                onClick = {
                                    if (emailOrUsername.isBlank() || password.length < 6) {
                                        localError = "يرجى ملء الحساب وإدخال 6 خانات على الأقل"
                                    } else {
                                        localError = null
                                        onResetPassword(emailOrUsername, password)
                                        authMode = AuthMode.LOGIN
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(onClick = { authMode = AuthMode.LOGIN }) {
                                Text("العودة لتسجيل الدخول", color = GoldLight, fontSize = 12.sp)
                            }
                        }
                    }

                    if (localError != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = localError!!,
                            color = ErrorRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notice about in-app virtual currency
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NavyCard.copy(alpha = 0.7f),
                border = BorderStroke(0.5.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚖️ إخلاء مسؤولية أساسي:",
                        color = GoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تطبيق جني المال يعتمد نقاطاً ومكافآت رقمية افتراضية (GANI COINS) داخل التطبيق فقط لفتح المزايا والألقاب. لا يقدم التطبيق أموالاً حقيقية أو سحباً أو تحويلات نقدية.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = onViewPrivacy) {
                    Text("سياسة الخصوصية وشروط الاستخدام", color = GoldLight, fontSize = 12.sp)
                }
            }
        }
    }
}
