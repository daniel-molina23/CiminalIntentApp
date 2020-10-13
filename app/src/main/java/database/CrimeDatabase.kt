package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.criminalintent.Crime

//tells Room that this class represents the database in the app
//      anytime data is added from the crime class, increment the version
@Database(entities = [ Crime::class], version=2)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase(){//extends from RoomDatabase

    //when database is created, room will generate a conrete implementation
    //of the DAO to access.
    abstract fun crimeDao() : CrimeDao
}

//NECESSARY for database version migration
val migration_1_2 = object : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT''"
        )
    }
}