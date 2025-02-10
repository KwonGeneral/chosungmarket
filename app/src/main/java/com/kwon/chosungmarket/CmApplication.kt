package com.kwon.chosungmarket

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.initialize
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.kwon.chosungmarket.common.utils.KLog
import com.kwon.chosungmarket.di.dataModule
import com.kwon.chosungmarket.di.domainModule
import com.kwon.chosungmarket.di.navigationModule
import com.kwon.chosungmarket.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class CmApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Firebase 초기화
        Firebase.initialize(this)

        // Firestore 설정
        val settings = firestoreSettings {
            // 메모리 캐시 사용
            setLocalCacheSettings(memoryCacheSettings {})

            // 오프라인 데이터 지속 캐시 사용
            setLocalCacheSettings(persistentCacheSettings {})
        }

        // Firestore 인스턴스에 설정 적용
        Firebase.firestore.firestoreSettings = settings

        // Kakao SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))

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