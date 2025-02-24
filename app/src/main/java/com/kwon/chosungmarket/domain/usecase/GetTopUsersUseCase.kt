package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.common.utils.KLog
import com.kwon.chosungmarket.domain.model.UserData
import com.kwon.chosungmarket.domain.repository.HallOfFameRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * 상위 랭킹 유저 목록을 조회하는 UseCase
 * 상위 랭킹 유저 목록을 조회합니다.
 */
class GetTopUsersUseCase(
    private val hallOfFameRepositoryImpl: HallOfFameRepositoryImpl
) {
    /**
     * 상위 랭킹 유저 목록을 조회합니다.
     * 포인트를 기준으로 정렬된 상위 100명의 사용자를 반환합니다.
     *
     * @return 상위 랭킹 유저 목록
     */
    operator fun invoke(): Flow<List<UserData>> {
        return hallOfFameRepositoryImpl.getTopUserList()
            .map { topUserMap ->
                topUserMap.map {
                    it.copy(nickname = it.nickname.ifBlank { "익명" })
                }
            }
            .catch { e ->
                KLog.e("GetTopUsersUseCase error", e)
                emit(emptyList())
            }
    }
}