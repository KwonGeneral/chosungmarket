package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.domain.model.UserData
import com.kwon.chosungmarket.domain.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.Flow

class GetCurrentUserInfoUseCase(
    private val userRepositoryImpl: UserRepositoryImpl
) {
    fun invoke(): Flow<UserData?> = userRepositoryImpl.getCurrentUser()
}