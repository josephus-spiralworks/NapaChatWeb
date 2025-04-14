package com.spiralworks.napachat

import MAX_EMAIL_LENGTH
import MAX_PHONE_LENGTH
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spiralworks.napachat.data.Country
import com.spiralworks.napachat.data.countries
import com.spiralworks.napachat.ui.CountryPickerDialog
import completeSignInJs
import getCompleted
import getEmail
import getError
import getLoggedIn
import getSuccess
import getUserEmail
import isEmailValid
import isPhoneValid
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import logoutJs
import napachat.composeapp.generated.resources.Res
import napachat.composeapp.generated.resources.icon_nc
import onAuthChange
import org.jetbrains.compose.resources.painterResource
import sendLoginLinkJs

@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    var message by remember { mutableStateOf<String?>(null) }
    var logoutMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var loginStatus by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        onAuthChange { result ->
            val loggedIn = getLoggedIn(result)
            val email = getUserEmail(result)

            loginStatus = if (loggedIn) "Welcome back $email" else null
        }
    }
    LaunchedEffect(Unit) {
        val result: JsAny = completeSignInJs().await()
        val loggedIn = getCompleted(result)
        val email = getEmail(result)
        val error = getError(result)

        loginStatus = if (loggedIn) "Logged in as $email"
        else if (error != null) "Login failed: $error"
        else null

        loading = false
    }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFF1F1), Color(0xFFD7F8FC)),
                        start = Offset(0f, Float.POSITIVE_INFINITY), // bottom start
                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                LoadingDialog("Checking...")
            } else {
                if (loginStatus != null && loginStatus!!.startsWith("Logged in")) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(loginStatus!!)

                        Spacer(Modifier.height(16.dp))

                        Button(onClick = {
                            coroutineScope.launch {
                                val result: JsAny = logoutJs().await()
                                val success = getSuccess(result)
                                val error = getError(result)

                                logoutMessage = if (success) {
                                    loginStatus = null // force logout
                                    "Logged out"
                                } else {
                                    "Logout failed: $error"
                                }
                            }
                        }) {
                            Text("Logout")
                        }

                        logoutMessage?.let {
                            Text(it, color = if (it.startsWith("Logout failed")) Color.Red else Color.Gray)
                        }
                    }
                } else {
                    LoginCard()
                }
            }
        }
    }
}

@Composable
fun LoginCard() {
    var isPhoneMode by remember { mutableStateOf(true) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCountry by remember { mutableStateOf<Country?>(countries.find { it.name == "United States" }) }

    val cardShape = RoundedCornerShape(24.dp)

    Card(
        elevation = 8.dp,
        shape = cardShape,
        modifier = Modifier
            .padding(16.dp)
            .widthIn(max = 420.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.icon_nc),
                contentDescription = null,
                modifier = Modifier
                    .size(144.dp)
                    .padding(bottom = 16.dp),
                colorFilter = ColorFilter.tint(Color.Black)
            )

            Text("Verification Station!", style = MaterialTheme.typography.h6)
            Spacer(Modifier.height(8.dp))
            Text(
                "Take a quick sec to confirm your ${if (isPhoneMode) "phone number" else "email"} with NapaChat, and we'll get this conversation flowing!",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(Modifier.height(24.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TabButton("Phone", isPhoneMode, Modifier.weight(1f)) { isPhoneMode = true }
                TabButton("Email", !isPhoneMode, Modifier.weight(1f)) { isPhoneMode = false }
            }

            Spacer(Modifier.height(24.dp))

            if (isPhoneMode) {
                CountrySelector(selectedCountry) { selectedCountry = it }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { newValue ->
                        val digitsOnly = newValue.text.filter { it.isDigit() }
                        val trimmed = digitsOnly.take(MAX_PHONE_LENGTH)

                        phoneNumber = TextFieldValue(
                            text = trimmed,
                            selection = TextRange(trimmed.length) // Keep cursor at end
                        )
                    },
                    placeholder = { Text("Phone number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    colors = textFieldColors(backgroundColor = Color.White)
                )
            } else {
                OutlinedTextField(
                    value = email,
                    onValueChange = { newValue ->
                        val trimmed = newValue.text.take(MAX_EMAIL_LENGTH)
                        email = TextFieldValue(
                            text = trimmed,
                            selection = TextRange(trimmed.length)
                        )
                    },
                    placeholder = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    colors = textFieldColors(backgroundColor = Color.White)
                )
            }

            Spacer(Modifier.height(24.dp))

            val isNextEnabled = if (isPhoneMode) {
                isPhoneValid(phoneNumber.text)
            } else {
                isEmailValid(email.text)
            }

            val coroutineScope = rememberCoroutineScope()
            var loading by remember { mutableStateOf(false) }
            var showMessage by remember { mutableStateOf<String?>(null) }

            Button(
                onClick = {
                    coroutineScope.launch {
                        loading = true
                        val result = sendLoginLinkJs(email.text)
                        showMessage = result.getOrNull() ?: result.exceptionOrNull()?.message
                        loading = false
                    }
                },
                enabled = isEmailValid(email.text), // or however you're validating
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Next")
            }
            if(loading) {
                LoadingDialog("Sending login link")
            }

            showMessage?.let {
                Text(it, color = Color.Green, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

@Composable
fun TabButton(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val bgColor = if (selected) Color.DarkGray else Color.LightGray
    val textColor = if (selected) Color.White else Color.Black

    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable { onClick() },
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, color = textColor)
        }
    }
}

@Composable
fun CountrySelector(
    selectedCountry: Country?,
    onSelect: (Country) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedCountry?.let { "${it.name} (${it.code})" } ?: "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPicker = true },
            readOnly = true,
            enabled = false,
            colors = textFieldColors(backgroundColor = Color.White)
        )

        if (showPicker) {
            CountryPickerDialog(
                countries = countries,
                onDismiss = { showPicker = false },
                onCountrySelected = {
                    onSelect(it)
                    showPicker = false
                }
            )
        }
    }
}

@Composable
fun LoadingDialog(message: String = "Loading...") {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text(message, color = Color.White)
        }
    }
}