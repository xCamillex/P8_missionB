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
import com.openclassrooms.p8_vitesse.utils.FakeCandidatesProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Database(entities = [CandidateDto::class], version = 1)
@TypeConverters(BitmapConverter::class, InstantConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun candidateDao(): CandidateDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.d(TAG, "onOpen called")
            INSTANCE?.let { database ->
                scope.launch {
                    initDatabase(
                        database.candidateDao()

                    )
                }
            }
        }

        suspend fun initDatabase(
            candidateDao: CandidateDao
        ) {

            Log.d(TAG, "initializing Database")

            // Collecte le Flow pour obtenir la liste des candidats
            val candidates = candidateDao.getAllCandidates().firstOrNull()

            // Vérifiez si la liste est vide
            if (candidates.isNullOrEmpty()) {
                Log.d(TAG, "Adding initial candidates")
                val fakeCandidates = FakeCandidatesProvider.getFakeCandidates()
                candidateDao.insertCandidates(fakeCandidates)
                Log.d(TAG, "Initial candidates added")
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
