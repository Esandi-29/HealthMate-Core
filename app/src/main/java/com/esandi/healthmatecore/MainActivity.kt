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
import com.esandi.healthmatecore.data.GoalsRepository
import com.esandi.healthmatecore.data.HealthMateDatabase
import com.esandi.healthmatecore.data.HealthRecordRepository
import com.esandi.healthmatecore.records.RecordListViewModel
import com.esandi.healthmatecore.ui.theme.HealthMateCoreTheme
import kotlinx.coroutines.launch
import com.esandi.healthmatecore.navigation.HealthMateApp
import com.esandi.healthmatecore.profile.GoalsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = HealthMateDatabase.getDatabase(this)
        val repository = HealthRecordRepository(database.healthRecordDao())
        val goalsRepository = GoalsRepository(database.goalsDao())

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

            val goalsViewModel: GoalsViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        GoalsViewModel(goalsRepository)
                    }
                }
            )

            HealthMateCoreTheme {
                HealthMateApp(
                    viewModel = viewModel,
                    goalsViewModel = goalsViewModel
                )
            }
        }
    }
}

