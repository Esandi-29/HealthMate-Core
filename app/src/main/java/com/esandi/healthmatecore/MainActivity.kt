package com.esandi.healthmatecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esandi.healthmatecore.ui.theme.HealthMateCoreTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HealthMateCoreTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        WelcomeHeader()
                        StepCounter()
                    }
                }

            }
        }
    }
}

@Composable
fun StepCounter(modifier: Modifier = Modifier) {
    var steps by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "Steps today: $steps",
            fontSize = 24.sp
        )

        Row (horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { steps += 500 }
            ) {
                Text("Add 500 steps")
            }

            Button(
                onClick = { steps = 0 }
            ) {
                Text("Reset")
            }
        }
    }

}

@Composable
fun WelcomeHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "HealthMate",
            fontSize = 32.sp
        )

        Text(
            text = "Welcome back, Esandi",
            fontSize = 18.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeHeaderPreview() {
    WelcomeHeader()
}