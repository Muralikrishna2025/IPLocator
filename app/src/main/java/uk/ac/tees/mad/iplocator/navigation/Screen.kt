package uk.ac.tees.mad.iplocator.navigation

import kotlinx.serialization.Serializable

sealed class SubGraph {
    @Serializable
    data object AuthGraph : SubGraph()

    @Serializable
    data object HomeGraph : SubGraph()
}

sealed class Dest {
    @Serializable
    data object SplashScreen : Dest()

    @Serializable
    data object LoginScreen : Dest()

    @Serializable
    data object SignUpScreen : Dest()

    @Serializable
    data object HomeScreen : Dest()

    @Serializable
    data object SearchScreen : Dest()

    @Serializable
    data object MapScreen : Dest()

    @Serializable
    data object ProfileScreen : Dest()

}