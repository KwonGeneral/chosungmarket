package com.kwon.chosungmarket.di

import com.kwon.chosungmarket.domain.usecase.AutoLoginUseCase
import com.kwon.chosungmarket.domain.usecase.CreateQuizGroupUseCase
import com.kwon.chosungmarket.domain.usecase.DeleteQuizGroupUseCase
import com.kwon.chosungmarket.domain.usecase.GetCurrentUserInfoUseCase
import com.kwon.chosungmarket.domain.usecase.GetQuizGroupListUseCase
import com.kwon.chosungmarket.domain.usecase.GetQuizGroupUseCase
import com.kwon.chosungmarket.domain.usecase.GetQuizResultCountUseCase
import com.kwon.chosungmarket.domain.usecase.GetQuizResultUseCase
import com.kwon.chosungmarket.domain.usecase.GetTopQuizListUseCase
import com.kwon.chosungmarket.domain.usecase.GetTopUsersUseCase
import com.kwon.chosungmarket.domain.usecase.LogoutUseCase
import com.kwon.chosungmarket.domain.usecase.ProcessQuizResultUseCase
import com.kwon.chosungmarket.domain.usecase.SignInWithKakaoUseCase
import com.kwon.chosungmarket.domain.usecase.ToggleQuizLikeUseCase
import com.kwon.chosungmarket.domain.usecase.WithdrawUserUseCase
import org.koin.dsl.module

/**
 * 도메인 레이어의 의존성을 제공하는 Koin 모듈
 * UseCase 클래스들의 의존성을 관리합니다.
 */
val domainModule = module {
    // 인증 관련 UseCase
    factory { SignInWithKakaoUseCase(get(), get(), get()) }
    factory { AutoLoginUseCase(get(), get()) }
    factory { LogoutUseCase(get(), get()) }
    factory { WithdrawUserUseCase(get(), get(), get()) }
    factory { GetCurrentUserInfoUseCase(get()) }

    // 퀴즈 관련 UseCase
    factory { CreateQuizGroupUseCase(get(), get(), get()) }
    factory { GetQuizGroupListUseCase(get(), get()) }
    factory { GetTopQuizListUseCase(get(), get()) }
    factory { ToggleQuizLikeUseCase(get(), get()) }
    factory { GetQuizGroupUseCase(get(), get(), get()) }
    factory { GetQuizResultUseCase(get(), get(), get()) }
    factory { DeleteQuizGroupUseCase(get(), get(), get()) }
    factory { GetQuizResultCountUseCase(get()) }
    factory { ProcessQuizResultUseCase(get(), get(), get(), get()) }
    factory { GetTopUsersUseCase(get()) }
}