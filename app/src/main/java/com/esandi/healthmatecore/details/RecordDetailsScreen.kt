package com.esandi.healthmatecore.details

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.esandi.healthmatecore.data.HealthRecord
import com.esandi.healthmatecore.records.dayLabel
import com.esandi.healthmatecore.ui.theme.CaloriesTextDark
import com.esandi.healthmatecore.ui.theme.CaloriesTextLight
import com.esandi.healthmatecore.ui.theme.RingMintDark
import com.esandi.healthmatecore.ui.theme.RingMintLight
import com.esandi.healthmatecore.ui.theme.SleepTextDark
import com.esandi.healthmatecore.ui.theme.SleepTextLight
import com.esandi.healthmatecore.ui.theme.WaterTextDark
import com.esandi.healthmatecore.ui.theme.WaterTextLight
import java.time.LocalDate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.esandi.healthmatecore.R
import com.esandi.healthmatecore.details.RecordDetailsScreen

@Composable
fun RecordDetailsScreen(
    record: HealthRecord,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    val dark = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text("Back")
        }

        Text(
            text = "${dayLabel(record.date, LocalDate.now())} • ${record.time}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (record.steps > 0) {
                DetailLine(
                    icon = R.drawable.ic_steps,
                    color = if (dark) RingMintDark else RingMintLight,
                    label = "Steps",
                    value = "%,d".format(record.steps)
                )
            }

            if (record.calories > 0) {
                DetailLine(
                    icon = R.drawable.ic_calories,
                    color = if (dark) CaloriesTextDark else CaloriesTextLight,
                    label = "Calories",
                    value = "%,d".format(record.calories)
                )
            }

            if (record.waterMl > 0) {
                DetailLine(
                    icon = R.drawable.ic_water,
                    color = if (dark) WaterTextDark else WaterTextLight,
                    label = "Water",
                    value = "${record.waterMl} ml"
                )
            }

            if (record.sleepMinutes > 0) {
                val hours = record.sleepMinutes / 60
                val minutes = record.sleepMinutes % 60

                DetailLine(
                    icon = R.drawable.ic_sleep,
                    color = if (dark) SleepTextDark else SleepTextLight,
                    label = "Sleep",
                    value = "${hours}h ${minutes}m"
                )
            }
        }

        if (!record.note.isNullOrBlank()) {
            Text(
                text = "Note",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 4.dp
                )
            )

            Text(
                text = record.note.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.weight(1f)
            ) {
                Text("Edit")
            }

            Button(
                onClick = {
                    showDeleteConfirmation = true
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
            },
            title = {
                Text("Delete record?")
            },
            text = {
                Text("You can undo this right after.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DetailLine(
    icon: Int,
    color: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = color
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}