package com.example.dicodingevent.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingevent.data.EventRepository
import com.example.dicodingevent.ui.detail.DetailViewModel
import com.example.dicodingevent.ui.main.fragment.favoriteEvent.FavoriteEventViewModel
import com.example.dicodingevent.ui.main.fragment.finishedEvent.FinishedEventViewModel
import com.example.dicodingevent.ui.main.fragment.home.HomeViewModel
import com.example.dicodingevent.ui.main.fragment.upcomingEvent.UpcomingEventViewModel
import com.example.dicodingevent.utils.Injection

class ViewModelRepositoryFactory(private val repo: EventRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(FinishedEventViewModel::class.java) -> {
                return FinishedEventViewModel(repo) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                return HomeViewModel(repo) as T
            }
            modelClass.isAssignableFrom(UpcomingEventViewModel::class.java) -> {
                return UpcomingEventViewModel(repo) as T
            }
            modelClass.isAssignableFrom(FavoriteEventViewModel::class.java) -> {
                return FavoriteEventViewModel(repo) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                return DetailViewModel(repo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelRepositoryFactory? = null
        fun getInstance(context: Context): ViewModelRepositoryFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelRepositoryFactory(Injection.provideRepository(context))
            }
    }
}