package com.example.dicodingevent.ui.main.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.EventRepository
import com.example.dicodingevent.data.Result
import com.example.dicodingevent.data.remote.response.EventItem
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: EventRepository): ViewModel() {
    private val _isLoadingUpcoming = MutableLiveData<Boolean>()
    val isLoadingUpcoming: LiveData<Boolean> = _isLoadingUpcoming

    private val _isLoadingFinished = MutableLiveData<Boolean>()
    val isLoadingFinished: LiveData<Boolean> = _isLoadingFinished

    private val _eventListUpcoming = MutableLiveData<List<EventItem>>()
    val eventListUpcoming: LiveData<List<EventItem>> = _eventListUpcoming

    private val _eventListFinished = MutableLiveData<List<EventItem>>()
    val eventListFinished: LiveData<List<EventItem>> = _eventListFinished

    private val _messageUpcoming = MutableLiveData<String>()
    val messageUpcoming: LiveData<String> = _messageUpcoming

    private val _messageFinished = MutableLiveData<String>()
    val messageFinished: LiveData<String> = _messageFinished

    private val _isShowButtonRetryUpcoming = MutableLiveData<Boolean>()
    val isShowButtonRetryUpcoming: LiveData<Boolean> = _isShowButtonRetryUpcoming

    private val _isShowButtonRetryFinished = MutableLiveData<Boolean>()
    val isShowButtonRetryFinished: LiveData<Boolean> = _isShowButtonRetryFinished

    init {
        getUpcomingEventList()
        getFinishedEventList()
    }

    fun getUpcomingEventList() {
        _isLoadingUpcoming.value = true
        _isShowButtonRetryUpcoming.value = false
        _messageUpcoming.value = ""
        viewModelScope.launch {
            val result = repository.getUpcomingEventList(5)
            _isLoadingUpcoming.value = false
            when(result) {
                is Result.Success ->
                    if(result.data.isEmpty()) {
                        _messageUpcoming.value = "No upcoming events"
                    }else{
                        _eventListUpcoming.value = result.data
                    }
                is Result.Error -> {
                    _messageUpcoming.value = result.error
                    _isShowButtonRetryUpcoming.value = true
                }
            }
        }
    }

    fun getFinishedEventList() {
        _isLoadingFinished.value = true
        _isShowButtonRetryFinished.value = false
        _messageFinished.value = ""
        viewModelScope.launch {
            val result = repository.getFinishedEventList(limitNumber = 5)
            _isLoadingFinished.value = false
            when(result) {
                is Result.Success -> {
                    if(result.data.isEmpty()) {
                        _messageFinished.value = "No finished events"
                    }else{
                        _eventListFinished.value = result.data
                    }
                }
                is Result.Error -> {
                    _messageFinished.value = result.error
                    _isShowButtonRetryFinished.value = true
                }
            }
        }
    }
}