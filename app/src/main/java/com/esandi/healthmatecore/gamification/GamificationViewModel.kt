package com.esandi.healthmatecore.gamification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esandi.healthmatecore.data.BadgeRepository
import com.esandi.healthmatecore.data.EarnedBadge
import com.esandi.healthmatecore.data.Goals
import com.esandi.healthmatecore.data.GoalsRepository
import com.esandi.healthmatecore.data.HealthRecordRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class GamificationViewModel(
    private val recordsRepository: HealthRecordRepository,
    private val goalsRepository: GoalsRepository,
    private val badgeRepository: BadgeRepository
) : ViewModel() {

    // One-off notifications for newly earned badges
    private val _newBadges = MutableSharedFlow<String>()
    val newBadges: SharedFlow<String> = _newBadges

    // Observe records, goals and saved badges together
    val badges: StateFlow<List<Badge>> = combine(
        recordsRepository.getAllDailyTotals(),
        goalsRepository.getGoals().map { it ?: Goals() },
        badgeRepository.getEarnedBadges()
    ) { history, goals, saved ->

        // Calculate currently earned badges
        val calculated = GamificationCalculator.badges(
            history = history,
            goals = goals
        )

        // Create a lookup table of previously saved badges
        val savedByName = saved.associateBy { it.name }

        calculated.map { badge ->

            val savedDate = savedByName[badge.name]?.earnedDate

            when {
                // Already saved: keep the original earned date
                savedDate != null -> {
                    badge.copy(
                        earnedDate = LocalDate.parse(savedDate)
                    )
                }

                // Newly earned: save it and announce only if earned today
                badge.earnedDate != null -> {
                    badgeRepository.saveEarnedBadge(
                        EarnedBadge(
                            name = badge.name,
                            earnedDate = badge.earnedDate.toString()
                        )
                    )

                    if (badge.earnedDate == LocalDate.now()) {
                        _newBadges.emit(badge.name)
                    }

                    badge
                }

                // Still locked
                else -> badge
            }
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}
