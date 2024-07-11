package com.example.algorithmvisualizer.data.di

import android.content.Context
import com.example.algorithmvisualizer.data.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository =
        PreferencesRepository(context)


//    @Binds
//    internal fun bindPreferencesRepository(
//        preferencesRepo: PreferencesRepositoryImpl,
//    ): PreferencesRepository

}