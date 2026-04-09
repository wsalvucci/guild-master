package com.example.demo.domain.model

import com.example.demo.domain.model.tasks.RequestBoard

/**
 * Whether a player wishes to create a fishing guild, an
 * adventurers guild, or a romance guild, they should
 * all be possible via dynamic options available as well
 * as how the economy, world, and general sim reacts to
 * their setup.
 *
 * Guilds with a history of conquering fearsome beasts
 * will attract the best adventurers. Guilds with high
 * marriage rates and attractive members will invite
 * people searching for a partner more than any
 * particular job. Selling expensive furniture will
 * bring in skilled carpenters.
 */
data class Guild(
    val id: Long,
    val guildName: String,
    val createdAt: Long,
    val requestBoard: RequestBoard
)
