package uk.ac.tees.mad.iplocator.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uk.ac.tees.mad.iplocator.model.repository.AuthRepository
import uk.ac.tees.mad.iplocator.model.repository.NetworkRepository
import uk.ac.tees.mad.iplocator.ui.utils.NetworkConnectivityManager
import uk.ac.tees.mad.iplocator.viewmodel.LoginScreenViewModel
import uk.ac.tees.mad.iplocator.viewmodel.SignUpScreenViewModel
import uk.ac.tees.mad.iplocator.viewmodel.SplashScreenViewModel

val appModule = module {
    single { NetworkConnectivityManager(androidContext()) }
    single { NetworkRepository(get()) }

    single { FirebaseAuth.getInstance() }
    single { AuthRepository(get()) }

    // ViewModels
    viewModelOf(::SplashScreenViewModel)
    viewModelOf(::SignUpScreenViewModel)
    viewModelOf(::LoginScreenViewModel)
}