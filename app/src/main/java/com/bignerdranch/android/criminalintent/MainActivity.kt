package com.bignerdranch.android.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.util.UUID

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(),
    CrimeListFragment.Callbacks {
    //implement interface callbacks from fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = CrimeListFragment.newInstance()

            //creates and commits a fragment transaction
            //used to add, remove, attach, detach, or replace fragments in the fragment list
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }// end if
    }

    override fun onCrimeSelected(crimeId: UUID){
        val fragment = CrimeFragment.newInstance(crimeId)
        //creates an instance of CrimeFragment when a crime in
        // CrimeListFragment is pressed
        //add to back stack to then view the list again
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}