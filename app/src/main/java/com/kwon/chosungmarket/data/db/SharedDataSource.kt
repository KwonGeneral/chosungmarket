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

/** 데이터 저장소를 사용하기 위한 확장 프로퍼티 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chosungmarket_prefs")

/**
 * 앱의 로컬 데이터를 관리하는 DataStore 클래스
 * 자동 로그인 설정과 카카오 ID 등 민감하지 않은 사용자 데이터를 저장합니다.
 */
class SharedDataStore(private val context: Context) {
    /** 공유 데이터 저장소의 키 */
    companion object {
        // 카카오 ID
        private val KAKAO_ID = stringPreferencesKey("kakao_id")

        // 자동 로그인 설정
        private val AUTO_LOGIN_ENABLED = booleanPreferencesKey("auto_login_enabled")
    }

    /** 카카오 ID를 저장합니다. */
    suspend fun saveKakaoId(kakaoId: String) {
        context.dataStore.edit { preferences ->
            preferences[KAKAO_ID] = kakaoId
        }
    }

    /** 카카오 ID를 조회합니다. */
    fun getKakaoId(): Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[KAKAO_ID]
        }

    /** 자동 로그인 설정을 저장합니다. */
    suspend fun saveAutoLoginEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_LOGIN_ENABLED] = enabled
        }
    }

    /** 자동 로그인 설정을 조회합니다. */
    fun getAutoLoginEnabled(): Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[AUTO_LOGIN_ENABLED] ?: false
        }

    /** 공유 데이터 저장소를 초기화합니다. */
    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}