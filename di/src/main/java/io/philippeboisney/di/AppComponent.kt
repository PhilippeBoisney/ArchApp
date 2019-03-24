package io.philippeboisney.di

val appComponent = listOf(createRemoteModule("https://api.github.com/"), repositoryModule, featureHomeModule, featureDetailModule, localModule)