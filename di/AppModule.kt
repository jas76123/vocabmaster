package com.example.vocabmaster.di

import android.content.Context
import com.example.vocabmaster.data.AppDatabase
import com.example.vocabmaster.data.WordDao
import com.example.vocabmaster.repository.WordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideWordDao(database: AppDatabase): WordDao {
        return database.wordDao()
    }

    @Provides
    @Singleton
    fun provideWordRepository(wordDao: WordDao): WordRepository {
        return WordRepository(wordDao)
    }
}