package com.example.dayflow.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

private val ScreenBg = Color(0xFF070B14)
private val FieldBg = Color(0xFF111A2B)
private val FieldBorder = Color(0xFF222C42)
private val BarEmpty = Color(0xFF222C42)
private val TextWhite = Color.White
private val TextMuted = Color(0xFF8B93A7)
private val AccentBlue = Color(0xFF3B82F6)
private val LinkBlue = Color(0xFF6EA0FF)
private val LinkPurple = Color(0xFF8B7CFF)
private val ErrorRed = Color(0xFFFF5470)
private val SuccessGreen = Color(0xFF2DD4A0)
private val WarnOrange = Color(0xFFF5A524)
private val GradientStart = Color(0xFF6C5CE7)
private val GradientEnd = Color(0xFF2F6FE4)
private val BrandGradient = Brush.horizontalGradient(listOf(GradientStart, GradientEnd))

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
    navController: NavController,
) {
    val fullName by viewModel.fullName.collectAsStateWithLifecycle()
    val username by viewModel.username.collectAsStateWithLifecycle()
    val usernameStatus by viewModel.usernameStatus.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val confirmPassword by viewModel.confirmPassword.collectAsStateWithLifecycle()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsStateWithLifecycle()
    val acceptTerms by viewModel.acceptTerms.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isRegisterSuccess by viewModel.isRegisterSuccess.collectAsStateWithLifecycle()

    LaunchedEffect(isRegisterSuccess) {
        if (isRegisterSuccess) {
            navController.navigate("home") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    val strength = passwordStrength(password)
    val confirmMismatch = confirmPassword.isNotEmpty() && confirmPassword != password

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .drawBehind {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x335B4FE9), Color.Transparent),
                            center = Offset(size.width / 2f, 0f),
                            radius = size.width * 0.9f
                        )
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // هدر: دکمه برگشت (راست) و لوگو (چپ)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(FieldBg)
                            .border(1.dp, FieldBorder, RoundedCornerShape(14.dp))
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = TextWhite)) { append("Streak") }
                                withStyle(SpanStyle(color = AccentBlue)) { append("Up") }
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("ساخت حساب", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("فقط چند اطلاعات ساده، بعد شروع می‌کنیم.", color = TextMuted, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(24.dp))

                // نام نمایشی
                FieldLabel("یوزر نیم")
                Spacer(modifier = Modifier.height(8.dp))
                DarkField(
                    value = fullName,
                    onValueChange = viewModel::onFullNameChanged,
                    leadingIcon = Icons.Outlined.PersonOutline,
                    placeholder = "نامی که به شما نمایش داده میشود"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // نام کاربری یکتا
                val usernameOk = usernameStatus == UsernameStatus.Available
                val usernameTaken = usernameStatus == UsernameStatus.Taken
                FieldLabel("نام کاربری یکتا")
                Spacer(modifier = Modifier.height(8.dp))
                DarkField(
                    value = username,
                    onValueChange = viewModel::onUsernameChanged,
                    leadingIcon = Icons.Outlined.AlternateEmail,
                    isError = usernameTaken,
                    isSuccess = usernameOk,
                    keyboardType = KeyboardType.Ascii,
                    trailing = {
                        when (usernameStatus) {
                            UsernameStatus.Available -> Icon(
                                Icons.Filled.Check, null, tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )

                            UsernameStatus.Checking -> CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = TextMuted,
                                strokeWidth = 2.dp
                            )

                            else -> {}
                        }
                    },
                    placeholder = "نام یوزر نیم شما"
                )
                if (usernameOk) HelperText("این نام کاربری آزاد است.", SuccessGreen)
                if (usernameTaken) HelperText("این نام کاربری قبلاً گرفته شده.", ErrorRed)

                Spacer(modifier = Modifier.height(16.dp))

                // ایمیل
                FieldLabel("ایمیل")
                Spacer(modifier = Modifier.height(8.dp))
                DarkField(
                    value = email,
                    onValueChange = viewModel::onEmailChanged,
                    leadingIcon = Icons.Outlined.Email,
                    keyboardType = KeyboardType.Email,
                    placeholder = "ایمیل شما"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // رمز عبور
                FieldLabel("رمز عبور")
                Spacer(modifier = Modifier.height(8.dp))
                DarkField(
                    value = password,
                    onValueChange = viewModel::onPasswordChanged,
                    leadingIcon = Icons.Outlined.Lock,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailing = {
                        IconButton(onClick = viewModel::onTogglePasswordVisibility) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Outlined.VisibilityOff
                                else Icons.Outlined.Visibility,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    },
                    placeholder = "رمز عبور"
                )

                if (password.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    StrengthBar(strength)
                    val (label, color) = when {
                        password.length < 8 -> "رمز عبور کوتاه است. حداقل ۸ نویسه." to ErrorRed
                        strength <= 2 -> "رمز عبور متوسط است." to WarnOrange
                        else -> "رمز عبور خوب است. حداقل ۸ نویسه." to TextMuted
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(label, color = color, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // تکرار رمز عبور
                FieldLabel("تکرار رمز عبور")
                Spacer(modifier = Modifier.height(8.dp))
                DarkField(
                    value = confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChanged,
                    leadingIcon = Icons.Outlined.Lock,
                    isError = confirmMismatch,
                    placeholder = "تکرار رمز عبور",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None
                    else PasswordVisualTransformation()
                )
                if (confirmMismatch) HelperText("تکرار رمز عبور با رمز بالا یکسان نیست.", ErrorRed)

                Spacer(modifier = Modifier.height(20.dp))

                // قوانین
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.onAcceptTermsChanged(!acceptTerms) }
                ) {
                    TermsBox(checked = acceptTerms)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = TextMuted)) { append("با ") }
                            withStyle(SpanStyle(color = LinkBlue)) { append("شرایط استفاده") }
                            withStyle(SpanStyle(color = TextMuted)) { append(" و ") }
                            withStyle(SpanStyle(color = LinkBlue)) { append("حریم خصوصی") }
                            withStyle(SpanStyle(color = TextWhite)) { append(" موافقم.") }
                        },
                        fontSize = 12.sp
                    )
                }

                errorMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(msg, color = ErrorRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(28.dp))

                // دکمه
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(BrandGradient)
                        .clickable(enabled = !isLoading) { viewModel.onCreateAccountClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "ساخت حساب",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("قبلاً ثبت‌نام کرده‌ای؟ ", color = TextMuted, fontSize = 13.sp)
                    Text(
                        text = "ورود",
                        color = LinkPurple,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun HelperText(text: String, color: Color) {
    Spacer(modifier = Modifier.height(6.dp))
    Text(text, color = color, fontSize = 11.sp, textAlign = TextAlign.Start)
}

@Composable
private fun DarkField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector,
    isError: Boolean = false,
    isSuccess: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = TextMuted) },
        trailingIcon = trailing,
        singleLine = true,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            errorTextColor = TextWhite,
            cursorColor = AccentBlue,
            errorCursorColor = ErrorRed,
            focusedBorderColor = if (isSuccess) SuccessGreen else AccentBlue,
            unfocusedBorderColor = if (isSuccess) SuccessGreen else FieldBorder,
            errorBorderColor = ErrorRed,
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            errorContainerColor = FieldBg
        ),
        placeholder = { Text(placeholder, color = TextMuted) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun StrengthBar(score: Int) {
    val activeColor = when (score) {
        1 -> ErrorRed
        2 -> WarnOrange
        else -> SuccessGreen
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(4) { i ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (i < score) activeColor else BarEmpty)
            )
        }
    }
}

@Composable
private fun TermsBox(checked: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(7.dp))
            .then(
                if (checked) Modifier.background(
                    Brush.linearGradient(
                        listOf(
                            GradientStart,
                            AccentBlue
                        )
                    )
                )
                else Modifier
                    .background(FieldBg)
                    .border(1.dp, FieldBorder, RoundedCornerShape(7.dp))
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}