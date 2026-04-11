package com.example.demo.domain.model.worldsave

import com.example.demo.domain.model.items.ItemInstance
import com.example.demo.domain.model.skills.CharacterStatData

data class CharacterData(
    val uuid: String,
    val skills: List<CharacterStatData>,
    val storage: List<ItemInstance>
)
