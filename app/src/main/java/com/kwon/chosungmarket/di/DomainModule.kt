package com.kwon.chosungmarket.di

import com.kwon.chosungmarket.domain.usecase.*
import org.koin.dsl.module

val domainModule = module {
    factory { SignInWithKakaoUseCase(get(), get(), get()) }
    factory { AutoLoginUseCase(get(), get()) }
    factory { LogoutUseCase(get(), get()) }
    factory { WithdrawUserUseCase(get(), get(), get()) }
    factory { GetCurrentUserInfoUseCase(get()) }

    factory { CreateQuizGroupUseCase(get(), get(), get()) }
    factory { GetQuizGroupListUseCase(get()) }
    factory { GetTopQuizListUseCase(get()) }
    factory { ProcessQuizResultUseCase(get(), get(), get()) }
    factory { ToggleQuizLikeUseCase(get(), get()) }
    factory { GetQuizGroupUseCase(get(), get()) }
    factory { GetQuizResultUseCase(get(), get(), get()) }
}
