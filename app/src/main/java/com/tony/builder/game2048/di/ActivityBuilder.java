package com.tony.builder.game2048.di;

import com.tony.builder.game2048.view.GameBoardActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {
    //@ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    @ContributesAndroidInjector
    abstract GameBoardActivity contributeGameBoardActivity();
}
