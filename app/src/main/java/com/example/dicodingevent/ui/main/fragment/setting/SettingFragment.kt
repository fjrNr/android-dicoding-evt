package com.example.dicodingevent.ui.main.fragment.setting

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.WorkManager
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.dataStore
import com.example.dicodingevent.databinding.FragmentSettingBinding
import com.example.dicodingevent.ui.ViewModelPreferenceFactory
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import com.example.dicodingevent.utils.ReminderWorker
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireActivity(), "Notifications permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(), "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        workManager = WorkManager.getInstance(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = SettingPreferences.getInstance(requireActivity().application.dataStore)
        val viewModel = ViewModelProvider(this, ViewModelPreferenceFactory(pref))[SettingViewModel::class.java]

        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            binding.switchTheme.isChecked = isDarkModeActive
        }

        binding.switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.setThemeSettings(isChecked)
        }

        viewModel.getNotificationSettings().observe(viewLifecycleOwner) { isNotificationActive: Boolean ->
            binding.switchNotification.isChecked = isNotificationActive
        }

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        binding.switchNotification.setOnCheckedChangeListener { button: CompoundButton, isChecked: Boolean ->
            viewModel.setNotificationSettings(isChecked)
            if(button.isPressed) {
                if (isChecked) {
                    startPeriodicTask()
                } else {
                    cancelPeriodicTask()
                }
            }
        }
    }

    private fun startPeriodicTask() {
        val data = Data.Builder()
            .putString(ReminderWorker.EXTRA_NAME, "Upcoming event")
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        periodicWorkRequest = PeriodicWorkRequest.Builder(ReminderWorker::class.java, 1, TimeUnit.DAYS)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(periodicWorkRequest)
    }

    private fun cancelPeriodicTask() {
        if(this::periodicWorkRequest.isInitialized) {
            workManager.cancelWorkById(periodicWorkRequest.id)
        }
    }
}