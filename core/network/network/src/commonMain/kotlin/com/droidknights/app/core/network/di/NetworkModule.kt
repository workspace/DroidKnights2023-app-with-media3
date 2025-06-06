package com.droidknights.app.core.network.di

import com.droidknights.app.core.network.DroidKnightsNetwork
import com.droidknights.app.core.network.engine.provideHttpClientEngine
import org.koin.dsl.module

val coreNetworkModule = module {
    single { DroidKnightsNetwork(provideHttpClientEngine()) }
}
