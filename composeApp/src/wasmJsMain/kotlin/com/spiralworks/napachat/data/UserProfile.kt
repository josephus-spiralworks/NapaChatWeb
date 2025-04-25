package com.spiralworks.napachat.data

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val name: String="Josephus",
    val email: String,
    val profilePictureUrl: String?=null
)