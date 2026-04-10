package com.example.demo.domain.model.worldsave

import com.example.demo.domain.model.items.Item
import com.example.demo.domain.model.skills.CharacterStatData
import com.example.demo.domain.model.tasks.Task

data class CharacterData(
    val skills: List<CharacterStatData>,
    val storage: List<Item>
)
