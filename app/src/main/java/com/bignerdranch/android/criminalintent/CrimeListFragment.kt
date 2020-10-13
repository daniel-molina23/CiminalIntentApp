package com.bignerdranch.android.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.widget.Button
import androidx.lifecycle.Observer
import java.util.UUID


private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    /*
     * Required interface for hosting activities
     */
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null

    private lateinit var crimeRecyclerView: RecyclerView
    //if there are no crimes present:
    private lateinit var emptyCrimeText: TextView

    private lateinit var emptyCrimeButton: Button

    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    //Callback function used to manage fragments, as well as interface
    // and onDetach
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //stash the context, this is the activity's instance
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    //CrimeListFragment’s view is set up (fragment_crime_list), hook
    //up the view to the fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        //RecyclerView requires a LayoutManager to work!
        //Layout Manager positions every item and also defines scrolling
        //position list items vertically
        //GridLayoutManager to arrange items in a grid instead (learned later on)
        crimeRecyclerView =
            view.findViewById(R.id.crime_recycler_view) as RecyclerView

        //if there are no crimes present:
        emptyCrimeText = view.findViewById(R.id.no_crimes_listed) as TextView

        emptyCrimeButton = view.findViewById(R.id.empty_add_crime) as Button

        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        //takes from the LiveData
        crimeRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //observes until the LiveData's list of crimes gets updated
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer{ crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }//checks if crimes NOT null, if so, then prints log
            })

        //when the crimes are empty
        emptyCrimeButton.setOnClickListener{
            val crime = Crime()
            crimeListViewModel.addCrime(crime)
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    //function used for callbacks: fragment management
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    //for the AppCompat menu action
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun updateUI(crimes: List<Crime>) {
        checkEmptyCrimeList(crimes)

        if(crimes.isNotEmpty()) {
            //interconnect them
            adapter = CrimeAdapter(crimes)
            crimeRecyclerView.adapter = adapter
        }
    }

    private fun checkEmptyCrimeList(crimes: List<Crime>){
        if(crimes.isEmpty()){
            emptyCrimeText.visibility = View.VISIBLE
            emptyCrimeButton.visibility = View.VISIBLE
            emptyCrimeButton.isEnabled = true
        }
        else{
            emptyCrimeText.visibility = View.GONE
            emptyCrimeButton.visibility = View.GONE
            emptyCrimeButton.isEnabled = false
        }
    }

    //RecyclerView never creates View's by themselves,
    // it creates ViewHolders which bring their itemView's
    // along for the ride
    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView =
            itemView.findViewById(R.id.crime_title)

        private val dateTextView: TextView =
            itemView.findViewById(R.id.crime_date)

        private val solvedImageView: ImageView =
            itemView.findViewById(R.id.crime_solved)

        //handle presses for the view
        init {
            itemView.setOnClickListener(this)
        }

        //binding the list of objects to the view
        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title

            dateTextView.text = this.crime.date.toString()

            //visible if solved, invisible otherwise
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View) {
            //call the interface callback
            callbacks?.onCrimeSelected(crime.id)
        }
    }


    private inner class CrimeAdapter(var crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {

        //responsible for creating a view to display, wrapping the view
        //in a view holder, and returning the result
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)

            return CrimeHolder(view)
        }

        //responsible for populating a given holder with the crime
        //from a given position
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

        //When the recycler view needs to know how many items
        //are in the data set backing it
        override fun getItemCount() = crimes.size

    }

    //static reference
    companion object {
        // function that your activities can call
        // to get an instance of your fragment
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}