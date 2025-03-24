package com.example.dicodingevent.ui.main.fragment.favoriteEvent

import androidx.lifecycle.ViewModel
import com.example.dicodingevent.data.EventRepository

class FavoriteEventViewModel(private val repository: EventRepository) : ViewModel() {
    fun getEventList() = repository.getFavoriteEventList()
}