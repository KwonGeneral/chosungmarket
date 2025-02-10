package com.kwon.chosungmarket.di

import com.google.firebase.firestore.FirebaseFirestore
import com.kwon.chosungmarket.data.db.*
import com.kwon.chosungmarket.data.repository.*
import com.kwon.chosungmarket.domain.repository.*
import org.koin.dsl.module

val dataModule = module {
    single { FirebaseFirestore.getInstance() }

    single { FirebaseUserDb(get()) }
    single { FirebaseQuizGroupsDb(get()) }
    single { FirebaseQuizDb(get()) }
    single { FirebaseQuizResultsDb(get()) }
    single { FirebaseHallOfFameDb(get()) }
    single { SharedDataStore(get()) }

    single<UserRepositoryImpl> { UserRepository(get(), get()) }
    single<QuizRepositoryImpl> { QuizRepository(get(), get(), get()) }
    single<HallOfFameRepositoryImpl> { HallOfFameRepository(get(), get()) }
    single<PersistentStorageRepositoryImpl> { PersistentStorageRepository(get()) }
    single<SessionRepositoryImpl> { SessionRepository() }
    single<QuizResultRepositoryImpl> { QuizResultRepository(get(), get()) }
}