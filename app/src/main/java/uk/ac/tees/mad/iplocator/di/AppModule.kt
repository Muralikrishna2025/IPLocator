package uk.ac.tees.mad.iplocator.di

import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uk.ac.tees.mad.iplocator.model.repository.AuthRepository
import uk.ac.tees.mad.iplocator.model.repository.IpApiRepository
import uk.ac.tees.mad.iplocator.model.repository.IpstackRepository
import uk.ac.tees.mad.iplocator.model.repository.NetworkRepository
import uk.ac.tees.mad.iplocator.model.retrofit.IpApiRetrofitInstance
import uk.ac.tees.mad.iplocator.model.retrofit.IpstackRetrofitInstance
import uk.ac.tees.mad.iplocator.model.serviceapi.ipApiService
import uk.ac.tees.mad.iplocator.model.serviceapi.ipstackApiService
import uk.ac.tees.mad.iplocator.ui.utils.NetworkConnectivityManager
import uk.ac.tees.mad.iplocator.viewmodel.HomeScreenViewModel
import uk.ac.tees.mad.iplocator.viewmodel.LoginScreenViewModel
import uk.ac.tees.mad.iplocator.viewmodel.SearchScreenViewModel
import uk.ac.tees.mad.iplocator.viewmodel.SignUpScreenViewModel
import uk.ac.tees.mad.iplocator.viewmodel.SplashScreenViewModel

val appModule = module {

    single { NetworkConnectivityManager(androidContext()) }
    single { NetworkRepository(get()) }

    single { FirebaseAuth.getInstance() }
    single { AuthRepository(get()) }

    // Retrofit
    single<ipstackApiService> { IpstackRetrofitInstance.create() }
    single<ipApiService> { IpApiRetrofitInstance.create() }

    // Repository
    single { IpstackRepository(get()) }
    single { IpApiRepository(get()) }

    // ViewModels
    viewModelOf(::SplashScreenViewModel)
    viewModelOf(::SignUpScreenViewModel)
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::SearchScreenViewModel)
}