package com.example.demo.domain.repository

import com.example.demo.domain.model.Guild

interface GuildRepository {
    suspend fun getAll(): List<Guild>
    suspend fun insert(username: String): Long
    suspend fun delete(id: Long)
}