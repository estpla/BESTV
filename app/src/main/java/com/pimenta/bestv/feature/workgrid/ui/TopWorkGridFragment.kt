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

package com.pimenta.bestv.feature.workgrid.ui

import android.content.Context
import android.os.Bundle
import com.pimenta.bestv.BesTV
import com.pimenta.bestv.common.presentation.model.WorkViewModel
import com.pimenta.bestv.data.repository.MediaRepository

/**
 * Created by marcus on 11-02-2018.
 */
class TopWorkGridFragment : AbstractWorkGridFragment() {

    private lateinit var workType: MediaRepository.WorkType

    override fun onAttach(context: Context) {
        super.onAttach(context)
        BesTV.applicationComponent.getTopWorkGridFragmentComponent()
                .view(this)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.run {
            workType = getSerializable(TYPE) as MediaRepository.WorkType
            showProgress = getBoolean(SHOW_PROGRESS)
        }
    }

    override fun loadData() {
        presenter.loadWorksByType(workType)
    }

    override fun loadMorePages() {
        if (workType != MediaRepository.WorkType.FAVORITES_MOVIES) {
            super.loadMorePages()
        }
    }

    override fun refreshDada() {
        if (workType == MediaRepository.WorkType.FAVORITES_MOVIES) {
            super.loadMorePages()
        }
    }

    override fun onWorksLoaded(works: List<WorkViewModel>?) {
        if (workType == MediaRepository.WorkType.FAVORITES_MOVIES) {
            if (works != null) {
                rowsAdapter.setItems(works, null)
            }
            progressBarManager.hide()
            mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
            return
        }
        super.onWorksLoaded(works)
    }

    companion object {

        private const val TYPE = "TYPE"

        fun newInstance(workType: MediaRepository.WorkType, showProgress: Boolean) =
                TopWorkGridFragment().apply {
                    this.arguments = Bundle().apply {
                        putSerializable(TYPE, workType)
                        putBoolean(SHOW_PROGRESS, showProgress)
                    }
                    this.workType = workType
                    this.showProgress = showProgress
                }
    }
}