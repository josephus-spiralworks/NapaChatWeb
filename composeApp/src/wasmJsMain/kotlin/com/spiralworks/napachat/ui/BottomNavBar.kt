package com.spiralworks.napachat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(selected: String, onSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEEEEEE))
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavItem("Chats", selected == "Chats") { onSelected("Chats") }
        NavItem("Contacts", selected == "Contacts") { onSelected("Contacts") }
        NavItem("Settings", selected == "Settings") { onSelected("Settings") }
    }
}

@Composable
fun NavItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = if (isSelected) Color.Black else Color.Gray,
        modifier = Modifier.clickable { onClick() }
    )
}