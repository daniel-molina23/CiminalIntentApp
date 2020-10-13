package database

import androidx.room.TypeConverter
import java.util.UUID
import java.util.Date

class CrimeTypeConverters {

    @TypeConverter
    fun fromDate(date : Date?) : Long?{
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch : Long?) : Date? {
        //if the type is probably going to be NULL then make let statement
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun toUUID(uuid : String?) : UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?) : String? {
        return uuid?.toString()
    }
}