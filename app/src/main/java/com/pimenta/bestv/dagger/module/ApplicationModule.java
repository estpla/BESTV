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

package com.pimenta.bestv.dagger.module;

import android.app.Application;
import android.util.DisplayMetrics;

import com.pimenta.bestv.database.DatabaseHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by marcus on 07-02-2018.
 */
@Module(includes = {
        PreferenceModule.class,
        ImplModule.class,
        NetworkModule.class,
        PresenterModule.class,
        DaoModule.class
})
public class ApplicationModule {

    private Application mApplication;

    public ApplicationModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    public DatabaseHelper provideDatabaseHelper(Application application) {
        return new DatabaseHelper(application);
    }

}