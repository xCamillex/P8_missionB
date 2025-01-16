package com.openclassrooms.p8_vitesse.data.repository

import com.openclassrooms.p8_vitesse.domain.model.Candidate
import kotlinx.coroutines.flow.Flow

/**
 * Interface définissant les opérations CRUD (Create, Read, Update, Delete) sur les candidats.
 * Cette interface sert de contrat entre le domaine de l'application et la couche d'accès aux données,
 * permettant ainsi de séparer les préoccupations et d'assurer la modularité de l'architecture.
 */
interface CandidateRepository {

    /**
     * Récupère tous les candidats depuis la source de données.
     * Cette méthode renvoie un `Flow` contenant la liste des candidats sous forme de modèle de domaine.
     * Cela permet de gérer les données de manière asynchrone et réactive.
     *
     * @return Un flux contenant la liste des candidats sous forme de modèles de domaine.
     */
    fun getAllCandidates(): Flow<List<Candidate>>

    /**
     * Récupère les candidats filtrés selon les critères optionnels spécifiés.
     * Cette méthode permet de filtrer les candidats par leur statut de favori et par un nom (partiel ou complet).
     *
     * @param favorite Filtrer par statut de favori (true pour les favoris, false pour les non-favoris,
     *                ou null pour ignorer ce filtre).
     * @param name Filtrer par nom (partiel ou complet). Si null, ce filtre est ignoré.
     * @return Un flux contenant la liste des candidats qui correspondent aux critères de filtrage.
     */
    fun getCandidates(favorite: Boolean?, name: String?): Flow<List<Candidate>>

    /**
     * Récupère un candidat par son identifiant unique (ID).
     * La méthode retourne un flux contenant un candidat correspondant à l'ID fourni, ou `null`
     * si aucun candidat
     * n'est trouvé avec cet identifiant.
     *
     * @param id L'identifiant unique du candidat.
     * @return Un flux contenant le candidat correspondant, ou `null` si le candidat n'est pas trouvé.
     */
    fun getById(id: Long): Flow<Candidate?>

    /**
     * Ajoute un candidat à la base de données ou le met à jour si un candidat avec le même identifiant
     * existe déjà.
     * Cette méthode permet d'inserer un nouveau candidat ou de mettre à jour un candidat existant.
     *
     * @param candidate Le modèle de domaine représentant le candidat à insérer ou mettre à jour.
     * @return L'identifiant unique du candidat inséré ou mis à jour.
     */
    suspend fun insertCandidate(candidate: Candidate): Long

    /**
     * Met à jour les informations d'un candidat existant dans la base de données.
     * Cette méthode permet de modifier les informations d'un candidat.
     *
     * @param candidate Le modèle de domaine représentant le candidat à mettre à jour.
     * @return Le nombre de lignes affectées par la mise à jour.
     */
    suspend fun updateCandidate(candidate: Candidate): Int

    /**
     * Supprime un candidat de la base de données.
     * Cette méthode permet de supprimer un candidat en fonction de son modèle de domaine.
     *
     * @param candidate Le modèle de domaine représentant le candidat à supprimer.
     */
    suspend fun deleteCandidate(candidate: Candidate)

    /**
     * Supprime tous les candidats de la base de données.
     * Cette méthode permet de supprimer l'ensemble des candidats stockés.
     */
    suspend fun deleteAllCandidates()

    /**
     * Met à jour le statut de favori d'un candidat spécifique dans la base de données.
     * Cette méthode permet de marquer un candidat comme favori ou non-favori.
     *
     * @param id L'identifiant unique du candidat dont le statut de favori doit être mis à jour.
     * @param favorite Le nouveau statut de favori du candidat (true ou false).
     * @return Le nombre de lignes affectées par la mise à jour.
     */
    suspend fun updateFavoriteStatus(id: Long, favorite: Boolean): Int
}