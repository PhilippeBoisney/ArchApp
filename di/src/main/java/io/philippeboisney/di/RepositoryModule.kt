package io.philippeboisney.di

import io.philippeboisney.repository.UserRepository
import org.koin.dsl.module.module

val repositoryModule = module {
    factory { UserRepository(get(), get()) }
}