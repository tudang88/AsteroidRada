package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(
            this,
            MainViewModelFactory(activity.application)
        ).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        // bind adapter for RecyclerView
        binding.asteroidRecycler.adapter =
            AsteroidRecyclerViewAdapter(AsteroidRecyclerViewAdapter.OnAsteroidItemClickListener {
                viewModel.showDetailInfo(it)
            })
        // observer signal from view model then navigate to details
        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.showDetailInfoDone()
            }
        })
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Option Menu handler
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.changeFilter(
            when (item.itemId) {
                R.id.show_saved_menu -> {
                    ObserverType.SAVED
                }
                R.id.show_today_menu -> {
                    ObserverType.TODAY
                }
                R.id.show_week_menu -> {
                    ObserverType.WEEKS
                }
                else -> {
                    ObserverType.TODAY
                }
            }
        )
        return true
    }
}
