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

package com.pimenta.bestv.feature.castdetail.presenter

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView

import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.pimenta.bestv.BuildConfig
import com.pimenta.bestv.feature.base.BasePresenter
import com.pimenta.bestv.manager.ImageManager
import com.pimenta.bestv.feature.castdetail.presenter.CastDetailsPresenter.View
import com.pimenta.bestv.repository.MediaRepository
import com.pimenta.bestv.repository.entity.Cast
import com.pimenta.bestv.repository.entity.CastMovieList
import com.pimenta.bestv.repository.entity.CastTvShowList
import com.pimenta.bestv.repository.entity.Work

import javax.inject.Inject

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers

/**
 * Created by marcus on 05-04-2018.
 */
class CastDetailsPresenter @Inject constructor(
        private val mediaRepository: MediaRepository,
        private val imageManager: ImageManager
) : BasePresenter<View>() {

    /**
     * Load the [Cast] details
     *
     * @param cast [Cast]
     */
    fun loadCastDetails(cast: Cast) {
        compositeDisposable.add(Single.zip<Cast, CastMovieList, CastTvShowList, CastInfo>(mediaRepository.getCastDetails(cast),
                mediaRepository.getMovieCreditsByCast(cast),
                mediaRepository.getTvShowCreditsByCast(cast),
                Function3<Cast, CastMovieList, CastTvShowList, CastInfo> { cast, castMovieList, castTvShowList ->
                    CastInfo(cast, castMovieList, castTvShowList)
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ castInfo ->
                    view.onCastLoaded(
                            castInfo.cast,
                            castInfo?.castMovieList?.works,
                            castInfo?.castTvShowList?.works
                    )
                }, { throwable ->
                    Log.e(TAG, "Error while getting the cast details", throwable)
                    view.onCastLoaded(null, null, null)
                }))
    }

    /**
     * Loads the [Cast] image
     *
     * @param cast [Cast]
     */
    fun loadCastImage(cast: Cast) {
        imageManager.loadImage(String.format(BuildConfig.TMDB_LOAD_IMAGE_BASE_URL, cast.profilePath),
                object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        view.onCardImageLoaded(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        view.onCardImageLoaded(null)
                    }
                })
    }

    /**
     * Loads the [Work] porter into [ImageView]
     *
     * @param work      [Work]
     * @param imageView [ImageView]
     */
    fun loadWorkPosterImage(work: Work, imageView: ImageView) {
        imageManager.loadImageInto(imageView,
                String.format(
                        BuildConfig.TMDB_LOAD_IMAGE_BASE_URL,
                        work.posterPath
                )
        )
    }

    /**
     * Wrapper class that keeps the cast info
     */
    private class CastInfo(
            val cast: Cast,
            val castMovieList: CastMovieList,
            val castTvShowList: CastTvShowList
    )

    interface View : BasePresenter.BaseView {

        fun onCastLoaded(cast: Cast?, movies: List<Work>?, tvShow: List<Work>?)

        fun onCardImageLoaded(resource: Drawable?)

    }

    companion object {

        private val TAG = CastDetailsPresenter::class.java.simpleName

    }
}