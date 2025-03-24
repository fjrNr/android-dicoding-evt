package com.example.dicodingevent.ui.main.fragment.upcomingEvent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.EventRepository
import com.example.dicodingevent.data.Result
import com.example.dicodingevent.data.remote.response.EventItem
import kotlinx.coroutines.launch

class UpcomingEventViewModel(private val repository: EventRepository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _eventList = MutableLiveData<List<EventItem>>()
    val eventList: LiveData<List<EventItem>> = _eventList

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isShowButtonRetry = MutableLiveData<Boolean>()
    val isShowButtonRetry: LiveData<Boolean> = _isShowButtonRetry

    init {
        getEventList()
    }

    fun getEventList() {
        _isLoading.value = true
        _isShowButtonRetry.value = false
        _message.value = ""
        viewModelScope.launch {
            val result = repository.getUpcomingEventList()
            _isLoading.value = false
            when(result) {
                is Result.Success -> {
                    if(result.data.isEmpty()) {
                        _message.value = "No upcoming events"
                    }else{
                        _eventList.value = result.data
                    }
                }
                is Result.Error -> {
                    _message.value = result.error
                    _isShowButtonRetry.value = true
                }
            }
        }
    }
}