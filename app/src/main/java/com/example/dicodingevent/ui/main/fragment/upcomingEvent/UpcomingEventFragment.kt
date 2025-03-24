package com.example.dicodingevent.ui.main.fragment.upcomingEvent

import android.content.res.Configuration
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.databinding.FragmentUpcomingEventBinding
import com.example.dicodingevent.data.remote.response.EventItem
import com.example.dicodingevent.databinding.ItemRowEventLargeBinding
import com.example.dicodingevent.ui.ViewModelRepositoryFactory
import com.example.dicodingevent.ui.main.adapter.ListEventAdapter

class UpcomingEventFragment : Fragment() {

    private var _binding: FragmentUpcomingEventBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UpcomingEventViewModel by viewModels {
        ViewModelRepositoryFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        viewModel.eventList.observe(viewLifecycleOwner){
            setEventListData(it)
        }
        viewModel.message.observe(viewLifecycleOwner){
            showMessage(it)

        }
        viewModel.isShowButtonRetry.observe(viewLifecycleOwner){
            showButtonRefresh(it)
        }
        binding.includeMessage.btnRetry.setOnClickListener{
            viewModel.getEventList()
        }
    }

    private fun setEventListData(listEvent: List<EventItem>) {
        with(binding.rvEvents) {
            layoutManager = if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(context, 2)
            }else{
                LinearLayoutManager(context)
            }
            adapter = ListEventAdapter(listEvent, ItemRowEventLargeBinding::inflate)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility =
            if (isLoading) {
                binding.includeMessage.root.visibility = View.GONE
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun showMessage(message: String) {
        with (binding) {
            if (message.isBlank()) {
                includeMessage.root.visibility = View.GONE
                rvEvents.visibility = View.VISIBLE
            } else {
                includeMessage.root.visibility = View.VISIBLE
                rvEvents.visibility = View.GONE
            }
            includeMessage.tvMessage.text = viewModel.message.value
        }
    }

    private fun showButtonRefresh(isShowing: Boolean) {
        binding.includeMessage.btnRetry.visibility = if (isShowing) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}