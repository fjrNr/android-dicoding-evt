package com.example.dicodingevent.ui.main.fragment.favoriteEvent

import android.content.res.Configuration
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.data.local.entity.FavoriteEventEntity
import com.example.dicodingevent.databinding.FragmentFavoriteEventBinding
import com.example.dicodingevent.databinding.ItemRowEventLargeBinding
import com.example.dicodingevent.ui.ViewModelRepositoryFactory
import com.example.dicodingevent.ui.main.adapter.ListEventAdapter

class FavoriteEventFragment : Fragment() {

    private var _binding: FragmentFavoriteEventBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteEventViewModel by viewModels {
        ViewModelRepositoryFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getEventList().observe(viewLifecycleOwner) {
            setEventListData(it)
            if(it.isNullOrEmpty()) {
                showMessage("No favorite events")
            }else{
                showMessage("")
            }
        }
    }

    private fun setEventListData(listEvent: List<FavoriteEventEntity>) {
        with(binding.rvEvents) {
            layoutManager = if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(context, 2)
            }else{
                LinearLayoutManager(context)
            }
            adapter = ListEventAdapter(listEvent, ItemRowEventLargeBinding::inflate)
        }
    }

    private fun showMessage(message: String) {
        with(binding) {
            if (message.isBlank()) {
                tvMessage.visibility = View.GONE
                rvEvents.visibility = View.VISIBLE
            } else {
                tvMessage.visibility = View.VISIBLE
                rvEvents.visibility = View.GONE
            }
            tvMessage.text = message
        }
    }
}