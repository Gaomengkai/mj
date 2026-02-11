package icu.merky.mj.data.repository

import icu.merky.mj.data.local.db.dao.DiaryEntryDao
import icu.merky.mj.data.local.db.entity.DiaryEntryEntity
import icu.merky.mj.domain.model.DiaryEntry
import icu.merky.mj.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DiaryRepositoryImpl @Inject constructor(
    private val diaryEntryDao: DiaryEntryDao
) : DiaryRepository {
    override fun observeRecentEntries(sessionId: Long, limit: Int): Flow<List<DiaryEntry>> {
        return diaryEntryDao.observeRecentEntries(sessionId, limit).map { entities ->
            entities.map { entity ->
                DiaryEntry(
                    id = entity.id,
                    sessionId = entity.sessionId,
                    content = entity.content,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun getRecentEntries(sessionId: Long, limit: Int): List<DiaryEntry> {
        return diaryEntryDao.getRecentEntries(sessionId, limit).map { entity ->
            DiaryEntry(
                id = entity.id,
                sessionId = entity.sessionId,
                content = entity.content,
                createdAt = entity.createdAt
            )
        }
    }

    override suspend fun addEntry(sessionId: Long, content: String, createdAt: Long) {
        diaryEntryDao.insert(
            DiaryEntryEntity(
                sessionId = sessionId,
                content = content,
                createdAt = createdAt
            )
        )
    }
}
