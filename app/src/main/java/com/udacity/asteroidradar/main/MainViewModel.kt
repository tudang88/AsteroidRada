package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

enum class ObserverType {
    TODAY,
    SAVED,
    WEEKS
}

enum class AsteroidLoadingStatus {
    LOADING,
    ERROR,
    DONE
}

class MainViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) return MainViewModel(app) as T
        throw IllegalArgumentException("Unable to construct ViewModel")
    }
}

/**
 * change to use AndroidViewModel
 * because the repository need application context to access database
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = MainViewModel::class.java.simpleName

    // database and repository
    private val database = AsteroidDatabase.getDatabase(application)
    private val repository = AsteroidsRepository(database)
    private val _asteroidList = repository.asteroidList
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList

    private val _imageOfDay = MutableLiveData<PictureOfDay>()
    val imageOfDay: LiveData<PictureOfDay>
        get() = _imageOfDay

    private val _status = MutableLiveData<AsteroidLoadingStatus>()
    val status: LiveData<AsteroidLoadingStatus>
        get() = _status

    /**
     * the live data for tracking
     * click event on list item then
     * navigate to detail fragment
     */
    private val _navigateToDetail = MutableLiveData<Asteroid>()
    val navigateToDetail: LiveData<Asteroid>
        get() = _navigateToDetail

    init {
        viewModelScope.launch {
            getImageOfDay()
            repository.initDataBase()
            changeFilter(ObserverType.TODAY)
        }
    }

    /**
     * Get image of day from Retrofit service
     */
    private fun getImageOfDay() {
        viewModelScope.launch {
            try {
                Timber.d("getImage of Day -> START")
                _imageOfDay.value = AsteroidApi.retrofitService.getImageOfDay()
                Timber.d("getImage of Day -> END")
            } catch (e: java.lang.Exception) {
                _imageOfDay.value = null
                Timber.d("getImage of Day -> FAILURE: ${e.localizedMessage}")
            }
        }
    }

    /**
     * interface for updating transition
     * to details fragment signal
     */
    fun showDetailInfo(asteroid: Asteroid) {
        _navigateToDetail.value = asteroid
    }

    /**
     * clear trigger after finish
     */
    fun showDetailInfoDone() {
        _navigateToDetail.value = null
    }

    /**
     * get asteroid base on filter type
     */
    fun changeFilter(filter: ObserverType) {
        _status.value = AsteroidLoadingStatus.LOADING
        viewModelScope.launch {
            repository.getAsteroids(filter)
            _status.value = AsteroidLoadingStatus.DONE
        }

    }
}