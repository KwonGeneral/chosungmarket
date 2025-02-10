package com.kwon.chosungmarket

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.initialize
import com.kakao.sdk.common.KakaoSdk
import com.kwon.chosungmarket.di.dataModule
import com.kwon.chosungmarket.di.domainModule
import com.kwon.chosungmarket.di.navigationModule
import com.kwon.chosungmarket.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

/**
 * 앱의 Application 클래스
 * 앱 수준의 초기화 작업을 수행합니다.
 */
class CmApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Firebase 초기화 및 설정
        Firebase.initialize(this)
        val settings = firestoreSettings {
            // 메모리 캐시 사용
            setLocalCacheSettings(memoryCacheSettings {})
            // 오프라인 데이터 지속 캐시 사용
            setLocalCacheSettings(persistentCacheSettings {})
        }
        Firebase.firestore.firestoreSettings = settings

        // Kakao SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))

        // Koin DI 설정
        startKoin {
            androidContext(this@CmApplication)
            modules(
                listOf(
                    navigationModule,
                    viewModelModule,
                    dataModule,
                    domainModule
                )
            )
        }
    }
}