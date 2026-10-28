package com.esandi.healthmatecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.esandi.healthmatecore.data.HealthMateDatabase
import com.esandi.healthmatecore.data.HealthRecordRepository
import com.esandi.healthmatecore.records.RecordListViewModel
import com.esandi.healthmatecore.ui.theme.HealthMateCoreTheme
import kotlinx.coroutines.launch
import com.esandi.healthmatecore.navigation.HealthMateApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = HealthMateDatabase.getDatabase(this)
        val repository = HealthRecordRepository(database.healthRecordDao())

        lifecycleScope.launch {
            repository.addSampleDataIfEmpty()
        }

        setContent {
            val viewModel: RecordListViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        RecordListViewModel(repository)
                    }
                }
            )

            HealthMateCoreTheme {
                HealthMateApp(viewModel = viewModel)
            }
        }
    }
}

