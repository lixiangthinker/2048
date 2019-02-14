package com.tony.builder.game2048.di;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tony.builder.game2048.GameApplication;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
public class AppModule {
    @Provides
    public SharedPreferences provideSharedPreferences(GameApplication context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
