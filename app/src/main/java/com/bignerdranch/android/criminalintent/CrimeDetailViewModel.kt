package com.bignerdranch.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.UUID

class CrimeDetailViewModel() : ViewModel(){
    //prevents from double data extraction from database
    // upon screen rotation

    //crimeRepository instance
    private val crimeRepository = CrimeRepository.get()
    //ID of the crime currently displayed
    private val crimeIdLiveData = MutableLiveData<UUID>()

    //since crimeLiveData is public, don't expose MutableLiveData
    var crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeId: UUID){
        crimeIdLiveData.value = crimeId
    }

    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }

    fun getPhotoFile(crime: Crime) : File {
        return crimeRepository.getPhotoFile(crime)
    }
}