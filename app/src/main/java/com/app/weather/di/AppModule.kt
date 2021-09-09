package com.app.weather.di

import com.app.weather.network.AppRepository
import com.app.weather.ui.main.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {

    //repo
    factory { AppRepository() }
    //view models
    viewModel { MainViewModel(get()) }

}