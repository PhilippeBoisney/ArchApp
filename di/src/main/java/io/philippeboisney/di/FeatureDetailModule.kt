package io.philippeboisney.di

import io.philippeboisney.detail.DetailViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val featureDetailModule = module {
    viewModel { DetailViewModel(get()) }
}