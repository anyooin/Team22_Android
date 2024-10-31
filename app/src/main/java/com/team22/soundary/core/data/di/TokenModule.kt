package com.team22.soundary.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.team22.soundary.core.data.TokenDatasource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TokenModule {
    private const val TOKEN_PREFERENCES_NAME = "token"

    @Provides
    @Singleton
    fun provideTokenDataStore(@ApplicationContext context: Context) : DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = {
            context.preferencesDataStoreFile(TOKEN_PREFERENCES_NAME)
        }
    )

    @Provides
    @Singleton
    fun provideTokenDatasource(tokenDataStore: DataStore<Preferences>) : TokenDatasource = TokenDatasource(tokenDataStore)

}