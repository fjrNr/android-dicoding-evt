package com.example.dicodingevent.ui.main.fragment.finishedEvent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.EventRepository
import com.example.dicodingevent.data.remote.response.EventItem
import com.example.dicodingevent.data.Result
import kotlinx.coroutines.launch

class FinishedEventViewModel(private val repository: EventRepository): ViewModel() {
    private val _isLoadingFinished = MutableLiveData<Boolean>()
    val isLoadingFinished: LiveData<Boolean> = _isLoadingFinished

    private val _eventListFinished = MutableLiveData<List<EventItem>>()
    val eventListFinished: LiveData<List<EventItem>> = _eventListFinished

    private val _messageFinished = MutableLiveData<String>()
    val messageFinished: LiveData<String> = _messageFinished

    private val _isShowButtonRetryFinished = MutableLiveData<Boolean>()
    val isShowButtonRetryFinished: LiveData<Boolean> = _isShowButtonRetryFinished



    private val _isLoadingFound = MutableLiveData<Boolean>()
    val isLoadingFound: LiveData<Boolean> = _isLoadingFound

    private val _eventListFound = MutableLiveData<List<EventItem>>()
    val eventListFound: LiveData<List<EventItem>> = _eventListFound

    private val _isShowMessageFound = MutableLiveData<Boolean>()
    val isShowMessageFound: LiveData<Boolean> = _isShowMessageFound

    private val _messageFound = MutableLiveData<String>()
    val messageFound: LiveData<String> = _messageFound

    init {
        getFinishedEventList()
    }

    fun getFinishedEventList() {
        _isLoadingFinished.value = true
        _isShowButtonRetryFinished.value = false
        _messageFinished.value = ""
        viewModelScope.launch {
            val result = repository.getFinishedEventList("")
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

    fun getFoundEventList(keyword: String) {
        _isLoadingFound.value = true
        _isShowMessageFound.value = false
        _messageFound.value = ""
        viewModelScope.launch {
            val result = repository.getAllEventList(keyword)
            _isLoadingFound.value = false
            when(result) {
                is Result.Success -> {
                    if(result.data.isEmpty()) {
                        _isShowMessageFound.value = true
                        _messageFound.value = "No found events"
                        _eventListFound.value = listOf()
                    }else{
                        _eventListFound.value = result.data
                    }
                }
                is Result.Error -> {
                    _isShowMessageFound.value = true
                    _messageFound.value = result.error
                }
            }
        }
    }
}