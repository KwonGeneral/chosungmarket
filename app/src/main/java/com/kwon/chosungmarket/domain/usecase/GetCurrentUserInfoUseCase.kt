package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.domain.model.UserData
import com.kwon.chosungmarket.domain.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 현재 로그인한 유저 정보를 가져오는 UseCase
 */
class GetCurrentUserInfoUseCase(
    private val userRepositoryImpl: UserRepositoryImpl
) {
    fun invoke(): Flow<UserData?> = userRepositoryImpl.getCurrentUser().map {
        it?.copy(nickname = it.nickname.ifBlank { "익명" })
    }
}