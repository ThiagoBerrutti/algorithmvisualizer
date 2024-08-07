package com.example.algorithmvisualizer.data.di

import android.content.Context
import com.example.algorithmvisualizer.data.repository.PreferencesRepositoryImpl
import com.example.algorithmvisualizer.domain.repository.PreferencesRepository
import dagger.Binds
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
        PreferencesRepositoryImpl(context)

}