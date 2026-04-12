package com.example.demo.data.repository

import com.example.demo.db.meta.SaveFile
import com.example.demo.db.meta.SaveFileQueries
import com.example.demo.domain.model.WorldSaveSlot
import com.example.demo.domain.repository.SaveSlotRepository
import com.example.demo.domain.util.ioDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.time.Clock

class SaveSlotRepositoryImpl(
    private val queries: SaveFileQueries,
    private val dispatcher: CoroutineDispatcher = ioDispatcher,
) : SaveSlotRepository {
    override suspend fun createSaveSlot(
        accountId: Long,
        slot: Long,
        name: String
    ): WorldSaveSlot = withContext(dispatcher) {
        val now = Clock.System.now().toEpochMilliseconds()

        queries.transaction {
            queries.insert(
                account_id = accountId,
                slot = slot,
                character_name = name,
                world_db_file = "pending.db",
                last_saved_at = now
            )

            val saveId = queries.lastInsertRowId().executeAsOne()
            val worldFile = "save_${saveId}.db"

            queries.updateWorldDbFile(
                id = saveId,
                world_db_file = worldFile
            )
        }

        val row = queries.selectByAccount(accountId)
            .executeAsList()
            .first { it.slot == slot }

        row.toDomain()
    }

    override suspend fun listSaveSlots(accountId: Long): List<WorldSaveSlot> = withContext(dispatcher) {
        queries.selectByAccount(accountId).executeAsList().map { it.toDomain() }
    }

    override suspend fun getSaveSlot(saveId: Long): WorldSaveSlot? = withContext(dispatcher) {
        queries.selectById(saveId).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun markSaved(saveId: Long, savedAtEpoch: Long, playtimeSeconds: Long) {
        withContext(dispatcher) {
            queries.markSaved(
                savedAtEpoch,
                playtimeSeconds,
                saveId,
            ).await()
        }
    }

    override suspend fun deleteSaveSlot(saveId: Long) {
        withContext(dispatcher) {
            queries.deleteById(saveId)
        }.await()
    }

}

private fun SaveFile.toDomain() = WorldSaveSlot(
    saveId = id,
    accountId = account_id,
    slot = slot.toInt(),
    characterName = character_name,
    worldDbFile = world_db_file,
    playtimeSeconds = playtime_seconds,
    lastSavedAt = last_saved_at,
)