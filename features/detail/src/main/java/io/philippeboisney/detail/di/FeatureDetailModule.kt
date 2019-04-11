package io.philippeboisney.detail.di

import io.philippeboisney.detail.DetailImageViewModel
import io.philippeboisney.detail.DetailViewModel
import io.philippeboisney.detail.domain.GetUserDetailUseCase
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val featureDetailModule = module {
    factory { GetUserDetailUseCase(get()) }
    viewModel { DetailViewModel(get(), get()) }
    viewModel { DetailImageViewModel() }
}