package com.openclassrooms.p8_vitesse.di

import com.openclassrooms.p8_vitesse.data.repository.CandidateRepository
import com.openclassrooms.p8_vitesse.data.repository.CandidateRepositoryImpl
import com.openclassrooms.p8_vitesse.data.repository.CurrencyRepository
import com.openclassrooms.p8_vitesse.data.repository.CurrencyRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt pour l'injection des dépendances des repositories dans l'application.
 * Ce module permet de lier les interfaces de repository aux leurs implémentations concrètes,
 * en assurant que des instances singleton sont fournies pour chaque repository.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Fournit une instance de `CandidateRepository`.
     * Cette méthode lie l'interface `CandidateRepository` à son implémentation concrète
     * `CandidateRepositoryImpl`.
     * L'annotation `@Singleton` garantit que la même instance de `CandidateRepositoryImpl`
     * sera utilisée tout au long du cycle de vie de l'application.
     *
     * @param repositoryImpl L'implémentation concrète de `CandidateRepository`.
     * @return L'instance de `CandidateRepository` (de type `CandidateRepositoryImpl`).
     */
    @Binds
    @Singleton
    abstract fun bindCandidateRepository(
        repositoryImpl: CandidateRepositoryImpl
    ): CandidateRepository

    /**
     * Fournit une instance de `CurrencyRepository`.
     * Cette méthode lie l'interface `CurrencyRepository` à son implémentation concrète
     * `CurrencyRepositoryImpl`.
     * L'annotation `@Singleton` garantit que la même instance de `CurrencyRepositoryImpl`
     * sera utilisée tout au long du cycle de vie de l'application.
     *
     * @param repositoryImpl L'implémentation concrète de `CurrencyRepository`.
     * @return L'instance de `CurrencyRepository` (de type `CurrencyRepositoryImpl`).
     */
    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        repositoryImpl: CurrencyRepositoryImpl
    ): CurrencyRepository
}