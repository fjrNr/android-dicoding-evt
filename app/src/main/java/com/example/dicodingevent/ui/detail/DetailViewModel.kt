package com.example.dicodingevent.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.EventRepository
import com.example.dicodingevent.data.Result
import com.example.dicodingevent.data.remote.response.EventItem
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: EventRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _eventItem = MutableLiveData<EventItem>()
    val eventItem: LiveData<EventItem> = _eventItem

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isShowMessage = MutableLiveData<Boolean>()
    val isShowMessage: LiveData<Boolean> = _isShowMessage

    private val _isShowButtonRetry = MutableLiveData<Boolean>()
    val isShowButtonRetry: LiveData<Boolean> = _isShowButtonRetry

    private val _isFavoriteEvent = MutableLiveData<Boolean>()
    val isFavoriteEvent: LiveData<Boolean> = _isFavoriteEvent

    fun getDetailEvent(id: Int) {
        _isLoading.value = true
        _isShowButtonRetry.value = false
        _isShowMessage.value = false
        _message.value = ""
        viewModelScope.launch {
            val result = repository.getDetailEvent(id)
            _isLoading.value = false
            when(result) {
                is Result.Success -> {
                    _eventItem.value = result.data
                    _isFavoriteEvent.value = repository.getFavoriteEvent(id)?.id == id
                }
                is Result.Error -> {
                    _message.value = result.error
                    _isShowButtonRetry.value = true
                    _isShowMessage.value = true
                }
            }
        }
    }

    fun addFavoriteEvent(event: EventItem) {
        viewModelScope.launch {
            repository.insertFavoriteEvent(event)
            _isFavoriteEvent.value = true
        }
    }

    fun removeFavoriteEvent(id: Int) {
        viewModelScope.launch {
            repository.deleteFavoriteEvent(id)
            _isFavoriteEvent.value = false
        }
    }
}