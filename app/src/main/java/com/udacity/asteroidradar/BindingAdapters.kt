package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.main.AsteroidLoadingStatus
import com.udacity.asteroidradar.main.AsteroidRecyclerViewAdapter

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("imageUrl")
fun bindImageViewToDisplayImageOfDay(imgView: ImageView, imgOfDay: PictureOfDay?) {
    imgOfDay?.let {
        val type = imgOfDay.mediaType
        if (type == "image" && imgOfDay.url != "") {
            val imgUri = imgOfDay.url.toUri().buildUpon().scheme("https").build()
            Picasso.with(imgView.context)
                .load(imgUri)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .into(imgView)
        }
    }
}

@BindingAdapter("listAsteroid")
fun bindRecyclerViewList(recyclerView: RecyclerView, data: List<Asteroid>?) {
    val adapter = recyclerView.adapter as AsteroidRecyclerViewAdapter
    adapter.submitList(data)
}

@BindingAdapter("loadingStatus")
fun bindingLoadingProgressBar(progressBars: ProgressBar, status: AsteroidLoadingStatus?) {
    when (status) {
        AsteroidLoadingStatus.LOADING -> {
            progressBars.visibility = View.VISIBLE
        }
        AsteroidLoadingStatus.DONE -> {
            progressBars.visibility = View.GONE
        }
        AsteroidLoadingStatus.ERROR -> {
            progressBars.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("hazardousDescription")
fun bindingHazardousStatusImage(imgView: ImageView, isHazardous: Boolean) {
    val context = imgView.context
    if (isHazardous) {
        imgView.contentDescription = context.getString(R.string.potential_hazardous_content_description)
    } else {
        imgView.contentDescription = context.getString(R.string.non_potential_hazardous_content_description)
    }
}