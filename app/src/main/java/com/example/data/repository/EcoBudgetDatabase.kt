package com.example.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EcoBudgetDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var instance: EcoBudgetDatabase? = null

        fun getInstance(context: Context): EcoBudgetDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    EcoBudgetDatabase::class.java,
                    "eco_budget.db"
                ).build().also { instance = it }
            }
    }
}
