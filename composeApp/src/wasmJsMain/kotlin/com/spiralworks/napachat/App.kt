package com.spiralworks.napachat

import LogoutResult
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spiralworks.napachat.data.UserProfile
import com.spiralworks.napachat.data.sampleChats
import com.spiralworks.napachat.data.storage.LocalStorageManager
import com.spiralworks.napachat.ui.BottomNavBar
import com.spiralworks.napachat.ui.ChatListItem
import com.spiralworks.napachat.ui.NetworkImage
import getCurrentUserJs
import getUserEmail
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import logoutJs

@Composable
fun App() {

    val coroutineScope = rememberCoroutineScope()
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val currentUser = getCurrentUserJs()
        if (currentUser != null) {
            println("Found active Firebase user!")
            val email = getUserEmail(currentUser)
//            val name = getUserName(currentUser)
//            val photoUrl = getUserPhotoUrl(currentUser)

            if (email != null) {
                userProfile = UserProfile(
//                    name = name ?: email.substringBefore("@"),
                    email = email
//                    profilePictureUrl = photoUrl ?: ""
                )
            }
        }
        loading = false
    }

    if (loading) {
        LoadingDialog("Loading...")
        return
    }



    if (userProfile != null) {
        ChatScreen(
            user = userProfile!!,
            onLogout = {
                coroutineScope.launch {
                    val result = logoutJs().await<LogoutResult>()
                    val success = result.success

                    if (success) {
                        LocalStorageManager.clearUserProfile()
                        userProfile = null
                    }
                }
            }
        )
    } else {
        AuthScreen(
            onLoginSuccess = { profile ->
                LocalStorageManager.saveUserProfile(profile)
                userProfile = profile
            }
        )
    }
}

@Composable
fun ChatScreen(user: UserProfile, onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf("Chats") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Brush.linearGradient(listOf(Color(0xFFFCF5E5), Color(0xFFD0F0E0))))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NetworkImage(
                    url = user.profilePictureUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(user.email, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    onLogout()
                }) {
                    Text("Logout")
                }
            }

        }

        // Search bar
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            leadingIcon = {
                androidx.compose.material.Icon(
                    Icons.Default.Search,
                    contentDescription = null
                )
            },
            shape = RoundedCornerShape(30.dp)
        )

        // Chat List
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(sampleChats) { chat ->
                ChatListItem(chat)
            }
        }

        // Bottom Navigation Replacement
        BottomNavBar(selected = selectedTab) { selectedTab = it }
    }
}