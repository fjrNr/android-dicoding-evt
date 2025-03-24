package com.example.dicodingevent.ui.main.fragment.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.utils.SettingPreferences
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SettingPreferences) : ViewModel() {

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun setThemeSettings(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getNotificationSettings(): LiveData<Boolean> {
        return pref.getNotificationSetting().asLiveData()
    }

    fun setNotificationSettings(isNotificationActive: Boolean) {
        viewModelScope.launch {
            pref.saveNotificationSetting(isNotificationActive)
        }
    }
}