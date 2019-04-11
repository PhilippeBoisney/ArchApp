package io.philippeboisney.archapp.di

import io.philippeboisney.detail.di.featureDetailModule
import io.philippeboisney.home.di.featureHomeModule
import io.philippeboisney.local.di.localModule
import io.philippeboisney.remote.di.createRemoteModule
import io.philippeboisney.repository.di.repositoryModule

val appComponent= listOf(createRemoteModule("https://api.github.com/"), repositoryModule, featureHomeModule, featureDetailModule, localModule)