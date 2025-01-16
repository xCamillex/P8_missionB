package com.openclassrooms.p8_vitesse.di

import android.content.Context
import com.openclassrooms.p8_vitesse.data.dao.CandidateDao
import com.openclassrooms.p8_vitesse.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


/**
 * Module Dagger pour la configuration et l'injection des dépendances liées à la base de données.
 * Ce module fournit des instances singleton pour la base de données et les DAO nécessaires pour
 * interagir avec les entités de l'application, et configure un CoroutineScope pour les opérations asynchrones.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Fournit un CoroutineScope pour exécuter des tâches asynchrones dans l'application.
     * Ce CoroutineScope utilise un `SupervisorJob` et `Dispatchers.Main` pour gérer les coroutines.
     * Il est utilisé pour les opérations asynchrones liées à la base de données, comme les requêtes DAO.
     *
     * @return Une instance singleton de CoroutineScope.
     */
    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /**
     * Fournit une instance de la base de données de l'application.
     * Cette méthode configure et crée la base de données `AppDatabase` en utilisant le contexte de
     * l'application et le CoroutineScope pour exécuter les opérations asynchrones liées à la base de données.
     *
     * @param context Le contexte de l'application, utilisé pour accéder à la base de données.
     * @param coroutineScope Un CoroutineScope pour les opérations asynchrones liées à la base de données.
     * @return Une instance singleton de `AppDatabase`.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        coroutineScope: CoroutineScope
    ): AppDatabase {
        return AppDatabase.getDatabase(context, coroutineScope)
    }

    /**
     * Fournit une instance de `CandidateDao` pour interagir avec les données des candidats dans la
     * base de données.
     * Cette méthode récupère le DAO pour les candidats à partir de l'instance de `AppDatabase`.
     *
     * @param appDatabase L'instance de `AppDatabase` à partir de laquelle récupérer le DAO.
     * @return Une instance de `CandidateDao`, qui permet d'effectuer des opérations sur les candidats.
     */
    @Provides
    fun provideCandidateDAO(appDatabase: AppDatabase): CandidateDao {
        return appDatabase.candidateDao()
    }
}