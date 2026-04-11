package com.example.demo.feature.game.utils

import com.example.demo.domain.model.skills.CharacterStat
import demo.feature.game.generated.resources.Bomb
import demo.feature.game.generated.resources.Mining
import demo.feature.game.generated.resources.Res
import demo.feature.game.generated.resources.Smithing
import demo.feature.game.generated.resources.Woodcutting
import org.jetbrains.compose.resources.DrawableResource

fun CharacterStat.getSkillIcon() : DrawableResource =
    when (this) {
        CharacterStat.Mining -> Res.drawable.Mining
        CharacterStat.Stamina -> Res.drawable.Mining
        CharacterStat.Smithing -> Res.drawable.Smithing
        CharacterStat.Woodcutting -> Res.drawable.Woodcutting
        else -> Res.drawable.Bomb
    }
