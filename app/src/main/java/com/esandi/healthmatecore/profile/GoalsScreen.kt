package com.esandi.healthmatecore.profile

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esandi.healthmatecore.R
import com.esandi.healthmatecore.ui.theme.CaloriesTextDark
import com.esandi.healthmatecore.ui.theme.CaloriesTextLight
import com.esandi.healthmatecore.ui.theme.HealthMateCoreTheme
import com.esandi.healthmatecore.ui.theme.RingMintDark
import com.esandi.healthmatecore.ui.theme.RingMintLight
import com.esandi.healthmatecore.ui.theme.SleepTextDark
import com.esandi.healthmatecore.ui.theme.SleepTextLight
import com.esandi.healthmatecore.ui.theme.WaterTextDark
import com.esandi.healthmatecore.ui.theme.WaterTextLight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.ui.draw.clip
import com.esandi.healthmatecore.ui.theme.Indigo

@Composable
fun GoalField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    icon: Int,
    iconColor: Color,
    unit: String,
    keyboardType: KeyboardType = KeyboardType.Number
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = iconColor
            )
        },
        suffix = { Text(unit) },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun GoalsScreen(
    initialSteps: Int = 8000,
    initialCalories: Int = 400,
    initialWater: Int = 2000,
    initialSleepMinutes: Int = 480,
    initialActiveDays: Int = 5,
    onSave: (steps: Int, calories: Int, water: Int, sleepMinutes: Int, activeDays: Int) -> Unit,
    onBack: () -> Unit
) {
    val dark = isSystemInDarkTheme()

    var stepsText by remember { mutableStateOf(initialSteps.toString()) }
    var caloriesText by remember { mutableStateOf(initialCalories.toString()) }
    var waterText by remember { mutableStateOf(initialWater.toString()) }
    var sleepText by remember {
        mutableStateOf(
            if (initialSleepMinutes % 60 == 0) (initialSleepMinutes / 60).toString()
            else (initialSleepMinutes / 60.0).toString()
        )
    }

    var stepsError by remember { mutableStateOf<String?>(null) }
    var caloriesError by remember { mutableStateOf<String?>(null) }
    var waterError by remember { mutableStateOf<String?>(null) }
    var sleepError by remember { mutableStateOf<String?>(null) }
    var activeDays by remember { mutableStateOf(initialActiveDays) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Back arrow + title
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Goals and Targets",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Text(
            text = "Daily goals",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        GoalField(
            label = "Steps",
            value = stepsText,
            onValueChange = { stepsText = it; stepsError = null },
            error = stepsError,
            icon = R.drawable.ic_steps,
            iconColor = if (dark) RingMintDark else RingMintLight,
            unit = "steps"
        )

        GoalField(
            label = "Calories burned",
            value = caloriesText,
            onValueChange = { caloriesText = it; caloriesError = null },
            error = caloriesError,
            icon = R.drawable.ic_calories,
            iconColor = if (dark) CaloriesTextDark else CaloriesTextLight,
            unit = "kcal"
        )

        GoalField(
            label = "Water",
            value = waterText,
            onValueChange = { waterText = it; waterError = null },
            error = waterError,
            icon = R.drawable.ic_water,
            iconColor = if (dark) WaterTextDark else WaterTextLight,
            unit = "ml"
        )

        GoalField(
            label = "Sleep",
            value = sleepText,
            onValueChange = { sleepText = it; sleepError = null },
            error = sleepError,
            icon = R.drawable.ic_sleep,
            iconColor = if (dark) SleepTextDark else SleepTextLight,
            unit = "hours",
            keyboardType = KeyboardType.Decimal
        )
        Text(
            text = "Active days per week",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (1..7).forEach { day ->
                val selected = day == activeDays
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) Indigo
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { activeDays = day },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selected) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Text(
            text = "Days a week you aim to meet your goals",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = {
                stepsError = GoalValidator.validateSteps(stepsText)
                caloriesError = GoalValidator.validateCalories(caloriesText)
                waterError = GoalValidator.validateWater(waterText)
                sleepError = GoalValidator.validateSleepHours(sleepText)

                val allValid = stepsError == null && caloriesError == null &&
                        waterError == null && sleepError == null

                if (allValid) {
                    onSave(
                        stepsText.toInt(),
                        caloriesText.toInt(),
                        waterText.toInt(),
                        GoalValidator.sleepHoursToMinutes(sleepText),
                        activeDays
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Save goals")
        }
    }
}

@Preview(showBackground = true, heightDp = 720)
@Composable
fun GoalsScreenPreview() {
    HealthMateCoreTheme {
        GoalsScreen(onSave = { _, _, _, _, _ -> }, onBack = {})
    }
}