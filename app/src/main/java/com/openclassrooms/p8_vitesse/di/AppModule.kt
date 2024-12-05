package com.openclassrooms.p8_vitesse.di

import android.content.Context
import androidx.room.Room
import com.openclassrooms.p8_vitesse.data.database.AppDatabase
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    // Fournit l'instance de la base de données Room
    private val DATABASE_NAME = "candidate_database"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }
    // Fournit le DAO pour accéder aux données
    @Provides
    @Singleton
    fun provideCandidateDao(database: AppDatabase) = database.candidateDao()

    /**
     * Fournit une instance de `CandidateRepository`.
     * @param repositoryImpl L'implémentation de CandidateRepository.
     */
    @Provides
    @Singleton
    fun provideCandidateRepository(
        repositoryImpl: CandidateRepositoryImpl
    ): CandidateRepository {
        return repositoryImpl
    }
}