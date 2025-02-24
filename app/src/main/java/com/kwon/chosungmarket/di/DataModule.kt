package com.kwon.chosungmarket.di

import com.google.firebase.firestore.FirebaseFirestore
import com.kwon.chosungmarket.data.db.FirebaseHallOfFameDb
import com.kwon.chosungmarket.data.db.FirebaseQuizDb
import com.kwon.chosungmarket.data.db.FirebaseQuizGroupsDb
import com.kwon.chosungmarket.data.db.FirebaseQuizResultsDb
import com.kwon.chosungmarket.data.db.FirebaseUserDb
import com.kwon.chosungmarket.data.db.SharedDataStore
import com.kwon.chosungmarket.data.repository.HallOfFameRepository
import com.kwon.chosungmarket.data.repository.PersistentStorageRepository
import com.kwon.chosungmarket.data.repository.QuizRepository
import com.kwon.chosungmarket.data.repository.QuizResultRepository
import com.kwon.chosungmarket.data.repository.SessionRepository
import com.kwon.chosungmarket.data.repository.UserRepository
import com.kwon.chosungmarket.domain.repository.HallOfFameRepositoryImpl
import com.kwon.chosungmarket.domain.repository.PersistentStorageRepositoryImpl
import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import com.kwon.chosungmarket.domain.repository.QuizResultRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import com.kwon.chosungmarket.domain.repository.UserRepositoryImpl
import org.koin.dsl.module

/**
 * 데이터 레이어의 의존성을 제공하는 Koin 모듈
 * Firebase, DataStore, Repository 구현체들의 의존성을 관리합니다.
 */
val dataModule = module {
    // Firebase 인스턴스 제공
    single { FirebaseFirestore.getInstance() }

    // Firebase DB 클래스들 제공
    single { FirebaseUserDb(get()) }
    single { FirebaseQuizGroupsDb(get()) }
    single { FirebaseQuizDb(get()) }
    single { FirebaseQuizResultsDb(get()) }
    single { FirebaseHallOfFameDb(get()) }

    // DataStore 제공
    single { SharedDataStore(get()) }

    // Repository 구현체들 제공
    single<UserRepositoryImpl> { UserRepository(get(), get()) }
    single<HallOfFameRepositoryImpl> { HallOfFameRepository(get(), get(), get()) }
    single<QuizRepositoryImpl> { QuizRepository(get(), get(), get()) }
    single<PersistentStorageRepositoryImpl> { PersistentStorageRepository(get()) }
    single<SessionRepositoryImpl> { SessionRepository() }
    single<QuizResultRepositoryImpl> { QuizResultRepository(get(), get(), get()) }
}