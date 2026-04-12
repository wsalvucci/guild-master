package com.example.demo.data.repository

import com.example.demo.db.world.CharacterMeta
import com.example.demo.db.world.PlayerCharacterStats
import com.example.demo.db.world.WorldDatabase
import com.example.demo.db.world.WorldDatabaseFileManager
import com.example.demo.db.world.WorldDatabaseManager
import com.example.demo.domain.model.Guild
import com.example.demo.domain.model.items.ItemCatalog
import com.example.demo.domain.model.items.OutputItemData
import com.example.demo.domain.model.items.ReqItemData
import com.example.demo.domain.model.worldsave.CharacterData
import com.example.demo.domain.model.skills.CharacterStat
import com.example.demo.domain.model.skills.CharacterStatData
import com.example.demo.domain.model.skills.CharacterStatKeys
import com.example.demo.domain.model.tasks.ReqStatData
import com.example.demo.domain.model.tasks.RequestBoard
import com.example.demo.domain.model.tasks.Task
import com.example.demo.domain.model.tasks.TaskCategory
import com.example.demo.domain.model.tasks.TaskTag
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
            db.characterDataQueries.upsertCharacter(
                worldSave.characterData.uuid
            )
            worldSave.flags.forEach { flag ->
                db.worldFlagsQueries.upsertFlag(
                    flag_key = flag.key,
                    enabled = if (flag.enabled) 1L else 0L
                )
            }
            worldSave.characterData.skills.forEach { skill ->
                db.playerCharacterStatsQueries.upsertSkill(
                    characterUuid = worldSave.characterData.uuid,
                    key = CharacterStatKeys.toString(skill.skill.key),
                    level = skill.level,
                    experience = skill.experience
                )
            }
            val meta = worldSave.characterMeta
            if (meta != null) {
                db.characterMetaQueries.upsertCharacterMeta(
                    characterUuid = meta.characterUuid,
                    name = meta.name
                )
            }
        }
        db.transaction {
            // UPSERT ALL ITEMS
            worldSave.characterData.storage.forEach { item ->
                db.itemsQueries.upsertItem(
                    uuid = item.uuid,
                    itemTag = item.tag,
                    quality = item.quality
                )
            }

            // CLEAR CHARACTER INVENTORIES
            db.characterInventoryQueries.clearCharacterInventory(worldSave.characterData.uuid)

            // READ ITEMS TO INVENTORY FROM MEMORY
            worldSave.characterData.storage.forEach { item ->
                db.characterInventoryQueries.addToCharacterInventory(
                    characterUuid = worldSave.characterData.uuid,
                    itemUuid = item.uuid
                )
            }

            // REMOVE ITEMS THAT ARE ORPHANS

            // THIS NEEDS UPDATED PER TABLE THAT HOLDS ITEM INSTANCE REFERENCES
            // OR ITEMS WILL BE LOST ON SAVE
            db.itemsQueries.purgeOrphans()
        }
        saveTasks(db, worldSave.activeTasks)
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
        val playerCharacterData = db.characterDataQueries.getCharacterByUuid(characterMeta?.characterUuid ?: "").executeAsOneOrNull()
        val playerCharacterStorage = db.characterInventoryQueries.getCharacterItems(characterMeta?.characterUuid ?: "").executeAsList()
        val playerCharacterItems = db.itemsQueries.transactionWithResult {
            playerCharacterStorage.mapNotNull { item ->
                db.itemsQueries.getItem(item.itemUuid).executeAsOneOrNull()
            }
        }

        val characterSkills = db.playerCharacterStatsQueries.getCharacterStats(
            characterUuid = playerCharacterData?.uuid ?: ""
        ).executeAsList()
        val skillKeys = characterSkills.associateBy { it.key }

        val metaData = saveSlotRepository.getSaveSlot(saveId)

        val loadedActiveTasks = loadTasks(db)

        return WorldSave(
            header = WorldSaveHeader(
                saveId = saveId,
                playtime = metaData?.playtimeSeconds ?: 0L,
                timestamp = metaData?.lastSavedAt ?: 0L
            ),
            flags = flags,
            characterMeta = characterMeta?.toDomain(),
            characterData = CharacterData(
                uuid = playerCharacterData?.uuid ?: "",
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
                storage = playerCharacterItems.map { item ->
                    val template = ItemCatalog.getByTagId(item.itemTag)
                    template.instantiate(
                        uuid = item.uuid,
                        quality = item.quality,
                    )
                }
            ),
            activeTasks = loadedActiveTasks,
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

    override suspend fun createNewCharacterMeta(
        characterUuid: String,
        name: String
    ) {
        val db = worldDatabaseManager.requireDatabase()

        db.transaction {
            db.characterMetaQueries.createNewCharacter(
                characterUuid = characterUuid,
                name = name,
            )
            db.characterDataQueries.upsertCharacter(
                uuid = characterUuid,
            )
        }
    }
}

private fun saveTasks(db: WorldDatabase, tasks: List<Task>) {
    db.transaction {
        tasks.forEach { task ->
            if (task.collected) {
                db.activeTasksQueries.deleteCollectedTask(
                    uuid = task.uuid,
                )
            } else {
                db.activeTasksQueries.upsertTask(
                    uuid = task.uuid,
                    name = task.name,
                    description = task.description,
                    category = task.category.key,
                    work_per_tick = task.workPerSecond,
                    started_at = task.startedAt,
                    total_work = task.totalWork,
                    is_background = if (task.isBackground) 1 else 0,
                )

                task.reqStatLevels.forEach { reqStatData ->
                    db.activeTaskReqStatsQueries.upsertActiveTaskReqStats(
                        task_uuid = task.uuid,
                        stat_key = reqStatData.stat.key.name,
                        min_stat_level = reqStatData.minLevel.toLong(),
                        bonus_above_level = reqStatData.bonusWorkAboveMinLevel.toLong(),
                        max_bonus_level = reqStatData.maxBonusLevel.toLong()
                    )
                }

                task.workCompletedPerCharacter.forEach { characterData ->
                    db.activeTaskCharacterWorkQueries.upsertActiveTaskCharacterWork(
                        task_uuid = task.uuid,
                        character_uuid = characterData.second,
                        work_completed = characterData.first
                    )
                }

                task.tags.forEach { tagData ->
                    db.activeTaskTagsQueries.upsertActiveTaskTags(
                        task_uuid = task.uuid,
                        task_tag = tagData.name,
                    )
                }

                task.reqItems.forEach { reqItemData ->
                    db.activeTaskRequiredItemsQueries.upsertActiveTaskRequiredItems(
                        task_uuid = task.uuid,
                        item_key = reqItemData.itemTemplate.tag,
                        quantity = reqItemData.quantity.toLong(),
                        min_quality = reqItemData.minQuality,
                    )
                }

                task.outputItems.forEach { outputItemData ->
                    db.activeTaskOutputItemsQueries.upsertActiveTaskOutputItems(
                        task_uuid = task.uuid,
                        item_key = outputItemData.itemTemplate.tag,
                        quantity = outputItemData.quantity.toLong(),
                        min_quality = outputItemData.minQuality,
                        max_quality = outputItemData.maxQuality,
                    )
                }

                task.experienceGain.forEach { experienceGainData ->
                    db.activeTaskExperienceGainsQueries.upsertActiveTaskExperienceGains(
                        task_uuid = task.uuid,
                        stat_key = experienceGainData.second.key.name,
                        experience_gain = experienceGainData.first.toLong()
                    )
                }
            }
        }
    }
}

private fun loadTasks(db: WorldDatabase): List<Task> {
    return db.activeTasksQueries.getTasks().executeAsList().map { taskData ->
        Task(
            uuid = taskData.uuid,
            startedAt = taskData.started_at,
            name = taskData.name,
            description = taskData.description,
            category = TaskCategory.fromKey(taskData.category),
            reqStatLevels = db.activeTaskReqStatsQueries.getActiveTaskReqState(task_uuid = taskData.uuid).executeAsList().map { reqStatData ->
                ReqStatData(
                    stat = CharacterStat.getFromKey(CharacterStatKeys.fromString(reqStatData.stat_key)),
                    minLevel = reqStatData.min_stat_level.toInt(),
                    bonusWorkAboveMinLevel = reqStatData.bonus_above_level.toInt(),
                    maxBonusLevel = reqStatData.max_bonus_level.toInt()
                )
            },
            workPerSecond = taskData.work_per_tick,
            totalWork = taskData.total_work,
            workCompletedPerCharacter = db.activeTaskCharacterWorkQueries.getActiveTaskCharacterWork(
                task_uuid = taskData.uuid
            ).executeAsList().map { characterWorkData ->
                characterWorkData.work_completed to characterWorkData.character_uuid
            },
            tags = db.activeTaskTagsQueries.getActiveTaskTags(
                task_uuid = taskData.uuid
            ).executeAsList().map { taskTagData ->
                TaskTag.fromKey(taskTagData.task_tag)
            },
            reqItems = db.activeTaskRequiredItemsQueries.getActiveTaskRequiredItems(
                task_uuid = taskData.uuid
            ).executeAsList().map { requiredItemData ->
                ReqItemData(
                    itemTemplate = ItemCatalog.getByTagId(requiredItemData.item_key),
                    quantity = requiredItemData.quantity.toInt(),
                    minQuality = requiredItemData.min_quality,
                )
            },
            outputItems = db.activeTaskOutputItemsQueries.getActiveTaskOutputItems(
                task_uuid = taskData.uuid
            ).executeAsList().map { outputItemData ->
                OutputItemData(
                    itemTemplate = ItemCatalog.getByTagId(outputItemData.item_key),
                    quantity = outputItemData.quantity.toInt(),
                    minQuality = outputItemData.min_quality,
                    maxQuality = outputItemData.max_quality,
                )
            },
            experienceGain = db.activeTaskExperienceGainsQueries.getActiveTaskExperienceGains(
                task_uuid = taskData.uuid
            ).executeAsList().map { experienceGainData ->
                experienceGainData.experience_gain.toInt() to CharacterStat.getFromKey(CharacterStatKeys.fromString(experienceGainData.stat_key))
            },
            isBackground = taskData.is_background == 1L,
            collected = false
        )
    }
}

private fun CharacterMeta.toDomain(): com.example.demo.domain.model.worldsave.CharacterMeta =
    com.example.demo.domain.model.worldsave.CharacterMeta(
        characterUuid = characterUuid,
        name = name,
    )

private fun PlayerCharacterStats.toDomain(): CharacterStatData {
    return CharacterStatData(
        skill = CharacterStat.getFromKey(CharacterStatKeys.fromString(this.key)),
        level = this.level,
        experience = this.experience
    )
}