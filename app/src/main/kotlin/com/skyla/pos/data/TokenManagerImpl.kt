package com.skyla.pos.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.skyla.pos.common.Constants
import com.skyla.pos.network.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "skyla_prefs")

class TokenManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : TokenManager {

    private val accessTokenKey = stringPreferencesKey(Constants.ACCESS_TOKEN_KEY)
    private val refreshTokenKey = stringPreferencesKey(Constants.REFRESH_TOKEN_KEY)
    private val userIdKey = stringPreferencesKey(Constants.USER_ID_KEY)
    private val userRoleKey = stringPreferencesKey(Constants.USER_ROLE_KEY)
    private val userNameKey = stringPreferencesKey(Constants.USER_NAME_KEY)

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
            preferences[refreshTokenKey] = refreshToken
        }
    }

    override suspend fun getAccessToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[accessTokenKey]
        }.first()
    }

    override suspend fun getRefreshToken(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[refreshTokenKey]
        }.first()
    }

    override suspend fun saveUserInfo(userId: String, role: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[userIdKey] = userId
            preferences[userRoleKey] = role
            preferences[userNameKey] = name
        }
    }

    override suspend fun getUserId(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[userIdKey]
        }.first()
    }

    override suspend fun getUserRole(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[userRoleKey]
        }.first()
    }

    override suspend fun getUserName(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[userNameKey]
        }.first()
    }

    override suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
