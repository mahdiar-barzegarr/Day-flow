package com.example.dayflow.di

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Singleton
class MutePrefs @Inject constructor(@ApplicationContext ctx: Context) {
    private val sp = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val _muted = MutableStateFlow(sp.getBoolean(KEY, false))
    val muted: StateFlow<Boolean> = _muted

    fun toggle() = set(!_muted.value)
    fun set(value: Boolean) {
        sp.edit().putBoolean(KEY, value).apply()
        _muted.value = value
    }

    companion object {
        private const val KEY = "app_muted"
        fun isMuted(ctx: Context) =
            ctx.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean(KEY, false)
    }
}