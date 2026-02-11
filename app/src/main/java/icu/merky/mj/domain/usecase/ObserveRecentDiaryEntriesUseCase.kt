package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.model.DiaryEntry
import icu.merky.mj.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRecentDiaryEntriesUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository
) {
    operator fun invoke(sessionId: Long, limit: Int = 5): Flow<List<DiaryEntry>> {
        return diaryRepository.observeRecentEntries(sessionId, limit)
    }
}
