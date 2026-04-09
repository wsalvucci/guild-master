package com.example.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo.domain.repository.ThemePreferenceRepository
import com.example.demo.domain.theme.AppThemeId
import com.example.demo.domain.theme.ThemeMode
import com.example.demo.domain.theme.ThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val repo: ThemePreferenceRepository
) : ViewModel() {
    val prefs = repo.preferences.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ThemePreferences()
    )

    fun setTheme(id: AppThemeId) = viewModelScope.launch { repo.setThemeId(id) }
    fun setMode(mode: ThemeMode) = viewModelScope.launch { repo.setThemeMode(mode) }
}