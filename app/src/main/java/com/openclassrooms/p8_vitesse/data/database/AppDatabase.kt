package com.openclassrooms.p8_vitesse.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.p8_vitesse.data.convert.BitmapConverter
import com.openclassrooms.p8_vitesse.data.convert.InstantConverter
import com.openclassrooms.p8_vitesse.data.dao.CandidateDao
import com.openclassrooms.p8_vitesse.data.entity.CandidateDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Représente la base de données de l'application pour la gestion des candidats.
 * Cette classe est utilisée pour gérer l'accès à la base de données via Room.
 *
 * Elle inclut une entité `CandidateDto` qui représente les candidats dans la base de données.
 *
 * La classe utilise un `TypeConverter` pour convertir des types spécifiques (comme `Bitmap` et
 * `Instant`) afin de les rendre compatibles avec Room.
 *
 * Lors de la création de la base de données, la méthode `onCreate` est appelée pour initialiser
 * la base de données avec des données par défaut.
 * Si la base de données est vide (aucun candidat n'est trouvé), une liste vide de candidats est insérée.
 * Si des candidats existent déjà, l'initialisation est ignorée pour éviter de modifier des données
 * existantes.
 *
 * Le mécanisme d'initialisation est géré par un callback (`AppDatabaseCallback`) qui est exécuté
 * lorsque la base de données est créée pour la première fois.
 * Cela garantit que les données par défaut ne sont insérées que lors de la première ouverture de
 * la base de données.
 */

@Database(entities = [CandidateDto::class], version = 1)
@TypeConverters(BitmapConverter::class, InstantConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun candidateDao(): CandidateDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d(TAG, "onCreate called")
            INSTANCE?.let { database ->
                scope.launch {
                    initDatabase(database.candidateDao())
                }
            }
        }

        // Méthode pour initialiser la base de données
        suspend fun initDatabase(candidateDao: CandidateDao) {
            Log.d(TAG, "Initializing Database")

            // Collecte la liste des candidats à partir de la base de données
            val candidates = candidateDao.getAllCandidates().firstOrNull()

            // Si la liste est vide ou nulle, on l'initialise avec une liste vide ou des candidats par défaut
            if (candidates.isNullOrEmpty()) {
                Log.d(TAG, "No candidates found, initializing with an empty list")
                // Insérer une liste vide (ou une liste par défaut si nécessaire)
                val emptyCandidates = emptyList<CandidateDto>()
                candidateDao.insertCandidates(emptyCandidates)
                Log.d(TAG, "Database initialized with an empty list of candidates")
            } else {
                Log.d(TAG, "Candidates already exist, skipping initialization")
            }
        }
    }

    companion object {
        private const val TAG = "AppDatabase"
        private const val DATABASE_NAME = "CandidateDB"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, coroutineScope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(AppDatabaseCallback(coroutineScope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}