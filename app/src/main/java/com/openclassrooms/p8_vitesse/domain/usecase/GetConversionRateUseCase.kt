package com.openclassrooms.p8_vitesse.domain.usecase

import com.openclassrooms.p8_vitesse.data.repository.CurrencyRepository
import javax.inject.Inject

/**
 * Use Case pour obtenir le taux de conversion EUR -> GBP.
 * Le ViewModel l'appelle pour obtenir le taux.
 */
class GetConversionRateUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {
    /**
     * Exécute le use case pour obtenir le taux.
     * @return Le taux EUR -> GBP.
     */
    suspend fun execute(): Double {
        return currencyRepository.getEurToGbpRate()
    }
}