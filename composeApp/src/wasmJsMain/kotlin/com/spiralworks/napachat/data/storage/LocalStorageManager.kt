package com.spiralworks.napachat.data.storage

import com.spiralworks.napachat.data.UserProfile
import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.dom.set

object LocalStorageManager {
    private const val USER_PROFILE_KEY = "userProfile"

    fun saveUserProfile(user: UserProfile) {
        val json = Json.encodeToString(user)
        localStorage[USER_PROFILE_KEY] = json
    }

    fun loadUserProfile(): UserProfile? {
        val json = localStorage[USER_PROFILE_KEY] ?: return null
        return try {
            Json.decodeFromString<UserProfile>(json)
        } catch (e: Exception) {
            null
        }
    }

    fun clearUserProfile() {
        localStorage.removeItem(USER_PROFILE_KEY)
    }
}