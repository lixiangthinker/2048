package com.tony.builder.game2048.di;

import com.tony.builder.game2048.viewmodel.GameBoardViewModel;
import com.tony.builder.game2048.viewmodel.GameViewModelFactory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract public class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(GameBoardViewModel.class)
    abstract ViewModel bindGameBoardViewModel(GameBoardViewModel gameBoardViewmodel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(GameViewModelFactory factory);
}
