package io.philippeboisney.detail.di

import io.philippeboisney.detail.DetailImageViewModel
import io.philippeboisney.detail.DetailViewModel
import io.philippeboisney.detail.domain.GetUserDetailUseCase
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featureDetailModule = module {
    factory { GetUserDetailUseCase(get()) }
    viewModel { DetailViewModel(get(), get()) }
    viewModel { DetailImageViewModel() }
}