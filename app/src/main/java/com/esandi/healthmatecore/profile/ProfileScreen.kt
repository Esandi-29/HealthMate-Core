package com.esandi.healthmatecore.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esandi.healthmatecore.R
import com.esandi.healthmatecore.ui.theme.HealthMateCoreTheme
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import com.esandi.healthmatecore.ui.theme.ProfileLimeEnd
import com.esandi.healthmatecore.ui.theme.ProfileMintStart
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import com.esandi.healthmatecore.ui.theme.ProfileBlueEnd
import com.esandi.healthmatecore.ui.theme.ProfileBlueStart
import com.esandi.healthmatecore.ui.theme.ProfileCoralStart
import com.esandi.healthmatecore.ui.theme.ProfileMagentaEnd
import com.esandi.healthmatecore.ui.theme.ProfileAmberStart
import com.esandi.healthmatecore.ui.theme.ProfileOrangeEnd
import com.esandi.healthmatecore.ui.theme.ProfileVioletEnd
import com.esandi.healthmatecore.ui.theme.ProfileVioletStart
import com.esandi.healthmatecore.gamification.Badge
import java.time.LocalDate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import java.time.format.DateTimeFormatter
import com.esandi.healthmatecore.ui.theme.BadgeAmber
import com.esandi.healthmatecore.ui.theme.BadgePurplePink
import com.esandi.healthmatecore.ui.theme.Indigo
import com.esandi.healthmatecore.ui.theme.SoftViolet
import androidx.compose.foundation.layout.height

private data class BadgeStyle(
    val icon: Int,
    val start: Color,
    val end: Color
)

private fun badgeStyle(name: String): BadgeStyle = when (name) {
    "First 10k" -> BadgeStyle(
        R.drawable.ic_trophy,
        BadgeAmber,
        ProfileOrangeEnd
    )

    "7-day streak" -> BadgeStyle(
        R.drawable.ic_flame,
        ProfileCoralStart,
        ProfileMagentaEnd
    )

    "Perfect day" -> BadgeStyle(
        R.drawable.ic_star,
        ProfileMagentaEnd,
        BadgePurplePink
    )

    "100k steps" -> BadgeStyle(
        R.drawable.ic_medal,
        Indigo,
        SoftViolet
    )

    else -> BadgeStyle(
        R.drawable.ic_lock,
        Color.Gray,
        Color.Gray
    )
}

data class ProfileOption(
    val label: String,
    val icon: Int,
    val gradientStart: Color,
    val gradientEnd: Color,
    val route: String
)

val profileOptions = listOf(
    ProfileOption(
        "Personal Information",
        R.drawable.ic_person,
        ProfileBlueStart,
        ProfileBlueEnd,
        "personal_info"
    ),
    ProfileOption(
        "Goals and Targets",
        R.drawable.ic_goals,
        ProfileMintStart,
        ProfileLimeEnd,
        "goals"
    ),
    ProfileOption(
        "Notifications",
        R.drawable.ic_notifications,
        ProfileAmberStart,
        ProfileOrangeEnd,
        "notifications"
    ),
    ProfileOption(
        "App Settings",
        R.drawable.ic_settings,
        ProfileVioletStart,
        ProfileVioletEnd,
        "app_settings"
    ),
    ProfileOption(
        "Help and Support",
        R.drawable.ic_help,
        ProfileCoralStart,
        ProfileMagentaEnd,
        "help"
    )
)

@Composable
fun ProfileScreen(
    name: String,
    badges: List<Badge>,
    onOptionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ProfileHeader(
            name = name,
            subtitle = "Member since October 2026",
            onSettingsClick = { onOptionClick("app_settings") }
        )

        BadgesCard(badges = badges)

        Spacer(modifier = Modifier.height(16.dp))

        // The rounded card holding the five rows
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            profileOptions.forEachIndexed { index, option ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 58.dp, end = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                ProfileRow(
                    label = option.label,
                    icon = option.icon,
                    gradientStart = option.gradientStart,
                    gradientEnd = option.gradientEnd,
                    onClick = { onOptionClick(option.route) }
                )
            }
        }
    }
}

@Composable
fun ProfileHeader(
    name: String,
    subtitle: String,
    onSettingsClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(onClick = onSettingsClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = "App settings",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Row(
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_person),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ProfileRow(
    label: String,
    icon: Int,
    gradientStart: Color,
    gradientEnd: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .size(26.dp)
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithCache {
                    val brush = Brush.linearGradient(
                        colors = listOf(gradientStart, gradientEnd)
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(brush = brush, blendMode = BlendMode.SrcAtop)
                    }
                }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, heightDp = 640)
@Composable
fun ProfileScreenPreview() {
    HealthMateCoreTheme {
        ProfileScreen(
            name = "Esandi",
            badges = listOf(
                Badge(
                    name = "First 10k",
                    earnedDate = LocalDate.of(2026, 10, 27),
                    lockedHint = "Walk 10,000 steps"
                ),
                Badge(
                    name = "7-day streak",
                    earnedDate = LocalDate.of(2026, 10, 28),
                    lockedHint = "7 days in a row"
                ),
                Badge(
                    name = "Perfect day",
                    earnedDate = null,
                    lockedHint = "All 4 goals"
                ),
                Badge(
                    name = "100k steps",
                    earnedDate = null,
                    lockedHint = "62k so far"
                )
            ),
            onOptionClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun BadgesCardPreview() {
    HealthMateCoreTheme {
        BadgesCard(
            badges = listOf(
                Badge(
                    name = "First 10k",
                    earnedDate = LocalDate.of(2026, 10, 27),
                    lockedHint = "Walk 10,000 steps"
                ),
                Badge(
                    name = "7-day streak",
                    earnedDate = LocalDate.of(2026, 10, 28),
                    lockedHint = "7 days in a row"
                ),
                Badge(
                    name = "Perfect day",
                    earnedDate = null,
                    lockedHint = "All 4 goals"
                ),
                Badge(
                    name = "100k steps",
                    earnedDate = null,
                    lockedHint = "62k so far"
                )
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun BadgeItem(
    badge: Badge,
    modifier: Modifier = Modifier
) {
    val earned = badge.earnedDate != null
    val style = badgeStyle(badge.name)
    val formatter = DateTimeFormatter.ofPattern("d MMM")

    val bottomText = badge.earnedDate?.format(formatter)
        ?: badge.lockedHint

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (earned) {
                        Brush.linearGradient(
                            listOf(style.start, style.end)
                        )
                    } else {
                        SolidColor(MaterialTheme.colorScheme.surfaceVariant)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    if (earned) style.icon else R.drawable.ic_lock
                ),
                contentDescription = badge.name,
                modifier = Modifier.size(18.dp),
                tint = if (earned) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        Text(
            text = badge.name,
            style = MaterialTheme.typography.labelSmall,
            color = if (earned) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        Text(
            text = bottomText,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BadgesCard(
    badges: List<Badge>,
    modifier: Modifier = Modifier
) {
    val earnedCount = badges.count { it.earnedDate != null }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Badges",
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = "$earnedCount of ${badges.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                badges.forEach { badge ->
                    BadgeItem(
                        badge = badge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}