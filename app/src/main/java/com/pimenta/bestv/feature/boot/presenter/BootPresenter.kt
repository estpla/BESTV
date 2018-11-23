/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pimenta.bestv.feature.boot.presenter

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import com.pimenta.bestv.feature.base.BasePresenter

import com.pimenta.bestv.feature.recommendation.service.RecommendationService

import javax.inject.Inject

/**
 * Created by marcus on 06-03-2018.
 */
class BootPresenter @Inject constructor(
        private val application: Application,
        private val alarmManager: AlarmManager
) : BasePresenter<BasePresenter.BaseView>() {

    /**
     * Schedules the recommendation update
     */
    fun scheduleRecommendationUpdate() {
        val alarmIntent = PendingIntent.getService(application, 0, RecommendationService.newInstance(application), 0)
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, INITIAL_DELAY, AlarmManager.INTERVAL_HALF_HOUR, alarmIntent)
    }

    companion object {

        private const val INITIAL_DELAY: Long = 5000
    }
}