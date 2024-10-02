package com.dynamicdriller.velox.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynamicdriller.velox.db.Activity
import com.dynamicdriller.velox.other.SortType
import com.dynamicdriller.velox.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {
    private val runSortedByDate = mainRepository.getAllBicycleActivitySortedByDate()
    private val runSortedByDistance  = mainRepository.getAllBicycleActivitySortedByDistance()
    private val runSortedByCalories = mainRepository.getAllBicycleActivitySortedByCalories()
    private val runSortedByTimeInMillis = mainRepository.getAllBicycleActivitySortedByTimeInMillis()
    private val runSortedByAvgSpeedInKmh = mainRepository.getAllBicycleActivitySortedByAvgSpeedInKmh()


    val activities = MediatorLiveData<List<Activity>>()

    var sortType = SortType.DATE

    init {
        activities.addSource(runSortedByDate){ result->
            if (sortType==SortType.DATE){
                result?.let { activities.value = it }
            }
        }
        activities.addSource(runSortedByAvgSpeedInKmh){ result->
            if (sortType==SortType.AVG_SPEED){
                result?.let { activities.value = it }
            }
        }
        activities.addSource(runSortedByCalories){ result->
            if (sortType==SortType.CALORIES_BURNED){
                result?.let { activities.value = it }
            }
        }
        activities.addSource(runSortedByDistance){ result->
            if (sortType==SortType.DISTANCE){
                result?.let { activities.value = it }
            }
        }
        activities.addSource(runSortedByTimeInMillis){ result->
            if (sortType==SortType.RUNNING_TIME){
                result?.let { activities.value = it }
            }
        }
    }


    fun sortRuns(sortType: SortType)= when(sortType){
        SortType.DATE -> runSortedByDate.value?.let { activities.value = it }
        SortType.RUNNING_TIME -> runSortedByTimeInMillis.value?.let { activities.value = it }
        SortType.CALORIES_BURNED -> runSortedByCalories.value?.let { activities.value = it }
        SortType.AVG_SPEED -> runSortedByAvgSpeedInKmh.value?.let { activities.value = it }
        SortType.DISTANCE -> runSortedByDistance.value?.let { activities.value = it }
        else -> {}
    }.also { this.sortType = sortType }



    fun insertBiCycle(bicycle:Activity) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.insertBiCycle(bicycle)
    }

}