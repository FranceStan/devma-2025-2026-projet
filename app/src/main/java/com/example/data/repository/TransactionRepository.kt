package com.example.data.repository

import com.example.shared.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Interface du dépôt pour la gestion des transactions d'EcoBudget.
 * Définit le contrat d'accès asynchrone et réactif aux données.
 */
interface TransactionRepository : com.example.shared.data.repository.TransactionRepository {

    /**
     * Récupère le flux asynchrone et réactif de l'ensemble des transactions.
     *
     * @return [Flow] émettant la liste mise à jour des [Transaction].
     */
    override fun getTransactions(): Flow<List<Transaction>>

    /**
     * Enregistre une nouvelle transaction au sein du dépôt.
     *
     * @param transaction La transaction immuable à ajouter.
     */
    override suspend fun addTransaction(transaction: Transaction)

    /**
     * Met à jour une transaction existante au sein du dépôt.
     *
     * @param transaction La transaction modifiée.
     */
    override suspend fun updateTransaction(transaction: Transaction)

    /**
     * Supprime une transaction existante par son identifiant unique.
     *
     * @param id Identifiant de la transaction à supprimer.
     */
    override suspend fun deleteTransaction(id: String)
}
