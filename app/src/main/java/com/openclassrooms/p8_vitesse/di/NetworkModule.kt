package com.openclassrooms.p8_vitesse.di

import com.openclassrooms.p8_vitesse.data.remote.CurrencyApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Module Dagger pour la configuration et l'injection des dépendances liées au réseau.
 * Ce module configure les objets nécessaires pour interagir avec l'API (via Retrofit et Moshi),
 * et fournit des instances singleton des services réseau pour l'application.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // L'URL de base pour l'API des devises
    private const val BASE_URL = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/"

    /**
     * Fournit une instance de Moshi pour la conversion JSON.
     * Moshi est une bibliothèque utilisée pour la sérialisation et la désérialisation JSON.
     * Cette méthode crée un objet Moshi avec le support des classes Kotlin.
     *
     * @return Une instance singleton de Moshi.
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory()) // Ajout du support pour les classes Kotlin
            .build()
    }

    /**
     * Fournit une instance de Retrofit configurée avec un convertisseur Moshi.
     * Retrofit est utilisé pour effectuer des appels réseau et interagir avec l'API distante.
     * Cette méthode configure Retrofit avec l'URL de base de l'API et un convertisseur Moshi
     * pour la gestion des réponses JSON.
     *
     * @param moshi L'instance de Moshi à utiliser pour le convertisseur JSON.
     * @return Une instance singleton de Retrofit.
     */
    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)  // Définition de l'URL de base de l'API
            .addConverterFactory(MoshiConverterFactory.create(moshi))  // Ajout du convertisseur Moshi
            .build()
    }

    /**
     * Fournit une instance de `CurrencyApiService`, qui est l'interface utilisée pour effectuer
     * les appels réseau vers l'API des devises. Retrofit crée l'implémentation concrète de l'interface.
     *
     * @param retrofit L'instance de Retrofit utilisée pour créer l'API service.
     * @return Une instance singleton de `CurrencyApiService` pour interagir avec l'API des devises.
     */
    @Provides
    @Singleton
    fun provideCurrencyApiService(retrofit: Retrofit): CurrencyApiService {
        return retrofit.create(CurrencyApiService::class.java)
    }
}