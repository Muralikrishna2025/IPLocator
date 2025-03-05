package uk.ac.tees.mad.iplocator.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uk.ac.tees.mad.iplocator.model.repository.NetworkRepository
import uk.ac.tees.mad.iplocator.ui.utils.NetworkConnectivityManager
import uk.ac.tees.mad.iplocator.viewmodel.SplashScreenViewModel

val appModule = module {
    single { NetworkConnectivityManager(androidContext()) }
    single { NetworkRepository(get()) }

    // ViewModels
    viewModelOf(::SplashScreenViewModel)
}