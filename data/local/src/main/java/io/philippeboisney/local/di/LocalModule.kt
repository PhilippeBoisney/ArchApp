package io.philippeboisney.local.di

import io.philippeboisney.local.ArchAppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DATABASE = "DATABASE"

val localModule = module {
    single(named(DATABASE)) { ArchAppDatabase.buildDatabase(androidContext()) }
    factory { (get(named(DATABASE)) as ArchAppDatabase).userDao() }
}