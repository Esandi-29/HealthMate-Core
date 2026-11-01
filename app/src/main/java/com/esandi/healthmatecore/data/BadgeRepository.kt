package com.esandi.healthmatecore.data

import kotlinx.coroutines.flow.Flow

class BadgeRepository(
    private val earnedBadgeDao: EarnedBadgeDao
) {

    fun getEarnedBadges(): Flow<List<EarnedBadge>> {
        return earnedBadgeDao.getAllEarnedBadges()
    }

    suspend fun saveEarnedBadge(badge: EarnedBadge) {
        earnedBadgeDao.insert(badge)
    }
}