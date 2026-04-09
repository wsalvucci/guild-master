package com.example.demo.data.repository

import com.example.demo.db.meta.GuildQueries
import com.example.demo.domain.model.Guild
import com.example.demo.domain.model.tasks.RequestBoard
import com.example.demo.domain.repository.GuildRepository
import com.example.demo.domain.util.ioDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.time.Clock

class GuildRepositoryImpl(
    private val queries: GuildQueries,
    private val dispatcher: CoroutineDispatcher = ioDispatcher
) : GuildRepository {
    override suspend fun getAll(): List<Guild> = withContext(dispatcher) {
        queries.selectAll().executeAsList().map { it.toDomain() }
    }

    override suspend fun insert(username: String): Long = withContext(dispatcher) {
        queries.insert(username, Clock.System.now().toEpochMilliseconds())
        queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun delete(id: Long): Unit = withContext(dispatcher) {

        queries.deleteById(id).await()
    }

    private fun com.example.demo.db.meta.Guild.toDomain(): Guild = Guild(
        id = id,
        guildName = username,
        createdAt = created_at,
        requestBoard = RequestBoard(
            requests = emptyList() // TODO
        )
    )
}