package com.example.dayflow.di

import com.example.dayflow.repository.FakeFriendsRepository
import com.example.dayflow.repository.FriendsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FriendsModule {
    @Provides
    @Singleton
    fun provideFriendsRepository(): FriendsRepository = FakeFriendsRepository()
}