package com.example.dicodingevent.ui.main.fragment.home

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.databinding.FragmentHomeBinding
import com.example.dicodingevent.data.remote.response.EventItem
import com.example.dicodingevent.databinding.ItemRowEventLargeBinding
import com.example.dicodingevent.databinding.ItemRowEventMediumBinding
import com.example.dicodingevent.ui.ViewModelRepositoryFactory
import com.example.dicodingevent.ui.main.adapter.ListEventAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels {
        ViewModelRepositoryFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //load upcoming event list
        viewModel.isLoadingUpcoming.observe(viewLifecycleOwner) {
            showLoadingUpcoming(it)
        }
        viewModel.eventListUpcoming.observe(viewLifecycleOwner){
            setUpcomingEventListData(it)
        }
        viewModel.messageUpcoming.observe(viewLifecycleOwner) {
            showMessageUpcoming(it)
            binding.includeMessageUpcoming.tvMessage.text = viewModel.messageUpcoming.value
        }
        viewModel.isShowButtonRetryUpcoming.observe(viewLifecycleOwner){
            showButtonRetryUpcoming(it)
        }
        binding.includeMessageUpcoming.btnRetry.setOnClickListener{
            viewModel.getUpcomingEventList()
        }

        //load finished event list
        viewModel.isLoadingFinished.observe(viewLifecycleOwner) {
            showLoadingFinished(it)
        }
        viewModel.eventListFinished.observe(viewLifecycleOwner){
            setFinishedEventListData(it)
        }
        viewModel.messageFinished.observe(viewLifecycleOwner){
            showMessageFinished(it)
            binding.includeMessageFinished.tvMessage.text = viewModel.messageFinished.value
        }
        viewModel.isShowButtonRetryFinished.observe(viewLifecycleOwner){
            showButtonRetryFinished(it)
        }
        binding.includeMessageFinished.btnRetry.setOnClickListener{
            viewModel.getFinishedEventList()
        }
    }

    private fun setUpcomingEventListData(listEvent: List<EventItem>) {
        with(binding.rvUpcomingEvents) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = ListEventAdapter(listEvent, ItemRowEventMediumBinding::inflate)
        }
    }

    private fun setFinishedEventListData(listEvent: List<EventItem>) {
        with(binding.rvFinishedEvents) {
            layoutManager = if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(context, 2)
            }else{
                LinearLayoutManager(context)
            }
            adapter = ListEventAdapter(listEvent, ItemRowEventLargeBinding::inflate)
        }
    }

    private fun showLoadingUpcoming(isLoading: Boolean) {
        binding.pbUpcomingEvent.visibility =
            if (isLoading) {
                binding.includeMessageUpcoming.root.visibility = View.GONE
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun showLoadingFinished(isLoading: Boolean) {
        binding.pbFinishedEvent.visibility =
            if (isLoading) {
                binding.includeMessageFinished.root.visibility = View.GONE
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun showMessageUpcoming(message: String) {
        with(binding) {
            if (message.isBlank()) {
                includeMessageUpcoming.root.visibility = View.GONE
                rvUpcomingEvents.visibility = View.VISIBLE
            } else {
                includeMessageUpcoming.root.visibility = View.VISIBLE
                rvUpcomingEvents.visibility = View.GONE
            }
        }
    }

    private fun showMessageFinished(message: String) {
        with(binding) {
            if (message.isBlank()) {
                includeMessageFinished.root.visibility = View.GONE
                rvFinishedEvents.visibility = View.VISIBLE
            } else {
                includeMessageFinished.root.visibility = View.VISIBLE
                rvFinishedEvents.visibility = View.GONE
            }
        }
    }

    private fun showButtonRetryUpcoming(isShowing: Boolean) {
        binding.includeMessageUpcoming.btnRetry.visibility = if (isShowing) View.VISIBLE else View.GONE
    }

    private fun showButtonRetryFinished(isShowing: Boolean) {
        binding.includeMessageFinished.btnRetry.visibility = if (isShowing) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}