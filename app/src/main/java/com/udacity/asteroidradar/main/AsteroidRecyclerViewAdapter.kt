package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.databinding.AsteroidListItemBinding

class AsteroidRecyclerViewAdapter(private val onClickListener: OnAsteroidItemClickListener) :
    ListAdapter<Asteroid, AsteroidRecyclerViewAdapter.ViewHolder>(DiffCallback) {
    /**
     * DiffCallback for using ListAdapter
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem == newItem
        }

    }

    /**
     * implement view holder here
     */
    inner class ViewHolder(private val binding: AsteroidListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(asteroid: Asteroid, clickListener: OnAsteroidItemClickListener) {
            binding.asteroidItem = asteroid
            binding.clickListener = clickListener
            // the below method cause the property update to execute immediatly
            binding.executePendingBindings()
        }
    }

    /**
     * OnClickListener interface for each item
     */
    class OnAsteroidItemClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }

    /**
     * The mandatory methods of RecyclerView Adapter
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // need to use inflate with full parameter set
        return ViewHolder(
            AsteroidListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    /**
     * binding item to ViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.bind(asteroid, onClickListener)
    }
}