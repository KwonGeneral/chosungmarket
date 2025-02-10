package com.kwon.chosungmarket.data.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chosungmarket_prefs")

class SharedDataStore(private val context: Context) {
    companion object {
        private val KAKAO_ID = stringPreferencesKey("kakao_id")
        private val AUTO_LOGIN_ENABLED = booleanPreferencesKey("auto_login_enabled")
    }

    suspend fun saveKakaoId(kakaoId: String) {
        context.dataStore.edit { preferences ->
            preferences[KAKAO_ID] = kakaoId
        }
    }

    fun observeKakaoId(): Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[KAKAO_ID]
        }

    suspend fun saveAutoLoginEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_LOGIN_ENABLED] = enabled
        }
    }

    fun observeAutoLoginEnabled(): Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[AUTO_LOGIN_ENABLED] ?: false
        }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}