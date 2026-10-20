package com.example.dayflow.data.local


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

data class UserProfile(val name: String = "", val username: String = "", val bio: String = "")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val nameKey = stringPreferencesKey("name")
    private val usernameKey = stringPreferencesKey("username")
    private val bioKey = stringPreferencesKey("bio")

    val profile: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            name = prefs[nameKey].orEmpty(),
            username = prefs[usernameKey].orEmpty(),
            bio = prefs[bioKey].orEmpty()
        )
    }

    suspend fun saveProfile(name: String, username: String, bio: String) {
        context.dataStore.edit { prefs ->
            prefs[nameKey] = name.trim()
            prefs[usernameKey] = username.trim()
            prefs[bioKey] = bio.trim()
        }
    }
}