package com.example.dicodingevent.ui.main.fragment.finishedEvent

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.R
import com.example.dicodingevent.databinding.FragmentFinishedEventBinding
import com.example.dicodingevent.data.remote.response.EventItem
import com.example.dicodingevent.databinding.ItemRowEventLargeBinding
import com.example.dicodingevent.ui.ViewModelRepositoryFactory
import com.example.dicodingevent.ui.main.adapter.ListEventAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchView

class FinishedEventFragment : Fragment() {

    private var _binding: FragmentFinishedEventBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinishedEventViewModel by viewModels {
        ViewModelRepositoryFactory.getInstance(requireActivity())
    }
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFinishedEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        val titleBar = (activity as AppCompatActivity).supportActionBar

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText
                .setOnEditorActionListener { _, _, _ ->
                    viewModel.getFoundEventList(searchView.text.toString())
                    false
                }
            searchView.addTransitionListener { _, _, newState ->
                if(newState == SearchView.TransitionState.SHOWING || newState == SearchView.TransitionState.SHOWN) {
                    titleBar?.hide()
                    bottomNav.visibility = View.GONE
                }else{
                    titleBar?.show()
                    bottomNav.visibility = View.VISIBLE
                }
            }
        }

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(binding.searchView.isShowing) {
                    binding.searchView.hide()
                    titleBar?.show()
                    bottomNav.visibility = View.VISIBLE
                } else {
                    bottomNav.setSelectedItemId(R.id.navigation_home)
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        viewModel.isLoadingFinished.observe(viewLifecycleOwner) {
            showLoadingFinished(it)
        }
        viewModel.eventListFinished.observe(viewLifecycleOwner){
            setFinishedEventListData(it)
        }
        viewModel.messageFinished.observe(viewLifecycleOwner){
            showMessageFinished(it)
            binding.includeMessage.tvMessage.text = viewModel.messageFinished.value
        }
        viewModel.isShowButtonRetryFinished.observe(viewLifecycleOwner){
            showButtonRefresh(it)
        }
        binding.includeMessage.btnRetry.setOnClickListener{
            viewModel.getFinishedEventList()
        }

        viewModel.isLoadingFound.observe(viewLifecycleOwner){
            showLoadingFound(it)
        }
        viewModel.eventListFound.observe(viewLifecycleOwner){
            setFoundEventListData(it)
        }
        viewModel.isShowMessageFound.observe(viewLifecycleOwner){
            showMessageFound(it)
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

    private fun showLoadingFinished(isLoading: Boolean) {
        binding.pbFinishedEvent.visibility =
            if (isLoading) {
                binding.includeMessage.root.visibility = View.GONE
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun showMessageFinished(message: String) {
        with(binding) {
            if (message.isBlank()) {
                includeMessage.root.visibility = View.GONE
                rvFinishedEvents.visibility = View.VISIBLE
            } else {
                includeMessage.root.visibility = View.VISIBLE
                rvFinishedEvents.visibility = View.GONE
            }
            includeMessage.tvMessage.text = viewModel.messageFinished.value
        }
    }

    private fun showButtonRefresh(isShowing: Boolean) {
        binding.includeMessage.btnRetry.visibility = if (isShowing) View.VISIBLE else View.GONE
    }

    private fun setFoundEventListData(listEvent: List<EventItem>) {
        with(binding.includeFoundEvent.rvEvents) {
            if(listEvent.isEmpty()) {
                visibility = View.GONE
            } else {
                layoutManager = if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    GridLayoutManager(context, 2)
                }else{
                    LinearLayoutManager(context)
                }
                adapter = ListEventAdapter(listEvent, ItemRowEventLargeBinding::inflate)
                visibility = View.VISIBLE
            }
        }
    }

    private fun showLoadingFound(isLoading: Boolean) {
        with(binding.includeFoundEvent) {
            progressBar.visibility = if (isLoading) {
                rvEvents.visibility = View.GONE
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun showMessageFound(isShowing: Boolean) {
        with(binding.includeFoundEvent) {
            tvMessage.visibility = if (isShowing) View.VISIBLE else View.GONE
            tvMessage.text = viewModel.messageFound.value
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.isEnabled = false
        _binding = null
    }
}