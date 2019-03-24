package io.philippeboisney.di

import io.philippeboisney.home.HomeViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val featureHomeModule = module {
    viewModel { HomeViewModel(get()) }
}