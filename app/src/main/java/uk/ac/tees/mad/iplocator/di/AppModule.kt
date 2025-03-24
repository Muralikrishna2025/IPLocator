package uk.ac.tees.mad.iplocator.di

import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uk.ac.tees.mad.iplocator.model.repository.AuthRepository
import uk.ac.tees.mad.iplocator.model.repository.IpApiRepository
import uk.ac.tees.mad.iplocator.model.repository.IpLocationDataRepository
import uk.ac.tees.mad.iplocator.model.repository.IpstackRepository
import uk.ac.tees.mad.iplocator.model.repository.NetworkRepository
import uk.ac.tees.mad.iplocator.model.repository.SearchHistoryRepository
import uk.ac.tees.mad.iplocator.model.retrofit.IpApiRetrofitInstance
import uk.ac.tees.mad.iplocator.model.retrofit.IpstackRetrofitInstance
import uk.ac.tees.mad.iplocator.model.room.Converters
import uk.ac.tees.mad.iplocator.model.room.IpLocationDataConverters
import uk.ac.tees.mad.iplocator.model.room.IpLocationDataDB
import uk.ac.tees.mad.iplocator.model.room.SearchHistoryDB
import uk.ac.tees.mad.iplocator.model.serviceapi.ipApiService
import uk.ac.tees.mad.iplocator.model.serviceapi.ipstackApiService
import uk.ac.tees.mad.iplocator.ui.utils.NetworkConnectivityManager
import uk.ac.tees.mad.iplocator.viewmodel.HomeScreenViewModel
import uk.ac.tees.mad.iplocator.viewmodel.LoginScreenViewModel
import uk.ac.tees.mad.iplocator.viewmodel.MapScreenViewModel
import uk.ac.tees.mad.iplocator.viewmodel.ProfileScreenViewModel
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

    // Search History Database
    singleOf(::Converters)
    single {
        Room.databaseBuilder(
            androidApplication(), SearchHistoryDB::class.java, "search_history_db"
        ).addTypeConverter(Converters()).build()
    }
    single {
        val database = get<SearchHistoryDB>()
        database.searchHistoryDao()
    }

    // IP Location Data Database
    singleOf(::IpLocationDataConverters)
    single {
        Room.databaseBuilder(
            androidApplication(), IpLocationDataDB::class.java, "ip_location_data"
        ).addTypeConverter(IpLocationDataConverters()).build()
    }
    single {
        val database = get<IpLocationDataDB>()
        database.ipLocationDao()
    }

    // Repository
    single { IpstackRepository(get()) }
    single { IpApiRepository(get()) }
    single { SearchHistoryRepository(get()) }
    single { IpLocationDataRepository(get()) }

    // ViewModels
    viewModelOf(::SplashScreenViewModel)
    viewModelOf(::SignUpScreenViewModel)
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::SearchScreenViewModel)
    viewModelOf(::ProfileScreenViewModel)
    viewModelOf(::MapScreenViewModel)
}