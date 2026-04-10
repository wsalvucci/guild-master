package com.example.demo.data.repository

import com.example.demo.db.world.CharacterMeta
import com.example.demo.db.world.PlayerCharacterStats
import com.example.demo.db.world.WorldDatabaseFileManager
import com.example.demo.db.world.WorldDatabaseManager
import com.example.demo.domain.model.Guild
import com.example.demo.domain.model.worldsave.CharacterData
import com.example.demo.domain.model.skills.CharacterStat
import com.example.demo.domain.model.skills.CharacterStatData
import com.example.demo.domain.model.skills.CharacterStatKeys
import com.example.demo.domain.model.tasks.RequestBoard
import com.example.demo.domain.model.worldsave.WorldFlag
import com.example.demo.domain.model.worldsave.WorldSave
import com.example.demo.domain.model.worldsave.WorldSaveHeader
import com.example.demo.domain.repository.SaveSlotRepository
import com.example.demo.domain.repository.WorldSaveRepository

class WorldSaveRepositoryImpl(
    private val worldDatabaseManager: WorldDatabaseManager,
    private val saveSlotRepository: SaveSlotRepository,
    private val worldDatabaseFileManager: WorldDatabaseFileManager
) : WorldSaveRepository {
    override suspend fun open(saveId: Long) {
        worldDatabaseManager.open(saveId)
    }

    override suspend fun close() {
        worldDatabaseManager.close()
    }

    override suspend fun save(worldSave: WorldSave, saveId: Long) {
        worldDatabaseManager.open(saveId)
        val db = worldDatabaseManager.requireDatabase()

        db.transaction {
            worldSave.flags.forEach { flag ->
                db.worldFlagsQueries.upsertFlag(
                    flag_key = flag.key,
                    enabled = if (flag.enabled) 1L else 0L
                )
            }
            worldSave.characterData.skills.forEach { skill ->
                db.playerCharacterStatsQueries.upsertSkill(
                    key = CharacterStatKeys.toString(skill.skill.key),
                    level = skill.level,
                    experience = skill.experience
                )
            }
        }
    }

    override suspend fun load(saveId: Long): WorldSave {
        worldDatabaseManager.open(saveId)
        val db = worldDatabaseManager.requireDatabase()

        val flags = db.worldFlagsQueries
            .selectAllFlags()
            .executeAsList()
            .map { flag ->
                WorldFlag(
                    id = flag.id,
                    key = flag.flag_key,
                    enabled = flag.enabled != 0L
                )
            }

        val characterMeta = db.characterMetaQueries.getCharacter().executeAsOneOrNull()

        val characterSkills = db.playerCharacterStatsQueries.getAll().executeAsList()
        val skillKeys = characterSkills.associateBy { it.key }

        val metaData = saveSlotRepository.getSaveSlot(saveId)

        return WorldSave(
            header = WorldSaveHeader(
                saveId = saveId,
                playtime = metaData?.playtimeSeconds ?: 0L,
                timestamp = metaData?.lastSavedAt ?: 0L
            ),
            flags = flags,
            characterMeta = characterMeta?.toDomain(),
            characterData = CharacterData(
                skills = CharacterStatKeys.entries
                    .filterNot { it == CharacterStatKeys.UNKNOWN }
                    .map { key ->
                        val foundData = skillKeys[CharacterStatKeys.toString(key)]
                        foundData?.toDomain()
                            ?: CharacterStatData(
                                skill = CharacterStat.getFromKey(key),
                                level = 1L,
                                experience = 0L
                            )
                    },
                storage = emptyList(), // TODO
            ),
            activeTasks = emptyList(), // TODO
            // TODO
            guild = Guild(
                id = 0L,
                guildName = "",
                createdAt = 0L,
                requestBoard = RequestBoard(
                    requests = emptyList(),
                )
            )
        )
    }

    override suspend fun initializeNewWorld(saveId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWorldFile(saveId: Long) {
        // If the file being deleted is the one currently open
        // make sure the connection is closed first.
        val curWorldSaveId = worldDatabaseManager.currentSaveId()
        if (curWorldSaveId == saveId) worldDatabaseManager.close()

        worldDatabaseFileManager.deleteDatabase(saveId)
    }

    override suspend fun createNewCharacterMeta(name: String) {
        val db = worldDatabaseManager.requireDatabase()

        db.characterMetaQueries.createNewCharacter(
            name = name,
        )
    }
}

private fun CharacterMeta.toDomain(): com.example.demo.domain.model.worldsave.CharacterMeta =
    com.example.demo.domain.model.worldsave.CharacterMeta(
        name = name,
    )

private fun PlayerCharacterStats.toDomain(): CharacterStatData {
    return CharacterStatData(
        skill = CharacterStat.getFromKey(CharacterStatKeys.fromString(this.key)),
        level = this.level,
        experience = this.experience
    )
}