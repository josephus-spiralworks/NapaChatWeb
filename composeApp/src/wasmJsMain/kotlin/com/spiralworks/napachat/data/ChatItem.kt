package com.spiralworks.napachat.data

data class ChatItem(
    val name: String,
    val lastMessage: String,
    val timestamp: String,
    val profileUrl: String?
)

val sampleChats = listOf(
    ChatItem("Josephus Work 2", "You: what", "Tue", null),
    ChatItem("new", "new: test 2", "03-06", null),
    ChatItem("马卡韦利", "You sent a photo", "02-18", null),
    ChatItem("testerako2222", "You: test", "02-17", null),
    ChatItem("january24", "january24: hello", "01-31", null),
)