package com.example.demo.data

import app.cash.sqldelight.db.SqlDriver
import com.example.demo.db.world.WorldDatabase
import com.example.demo.db.world.WorldDatabaseDriverFactory
import com.example.demo.db.world.WorldDatabaseManager
import com.example.demo.domain.repository.SaveSlotRepository
import com.example.demo.domain.util.ioDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class WorldDatabaseManagerImpl(
    private val factory: WorldDatabaseDriverFactory,
    private val saveSlotRepository: SaveSlotRepository,
    private val dispatcher: CoroutineDispatcher = ioDispatcher
) : WorldDatabaseManager {

    private var currentId: Long? = null
    private var driver: SqlDriver? = null
    private var db: WorldDatabase? = null

    override suspend fun open(saveId: Long) = withContext(dispatcher) {
        if (currentId == saveId && db != null) return@withContext

        val slot = requireNotNull(saveSlotRepository.getSaveSlot(saveId)) {
            "Save slot $saveId not found"
        }

        driver?.close()
        driver = null
        db = null
        currentId = null

        val newDriver = factory.createDriver(slot.worldDbFile)
        val newDb = WorldDatabase(newDriver)

        driver = newDriver
        db = newDb
        currentId = saveId
    }

    override suspend fun close() = withContext(dispatcher) {
        driver?.close()
        driver = null
        db = null
        currentId = null
    }

    override fun requireDatabase(): WorldDatabase =
        requireNotNull(db) { "World database is not open" }

    override fun currentSaveId(): Long? = currentId
}