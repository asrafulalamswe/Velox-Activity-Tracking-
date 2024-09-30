package com.dynamicdriller.velox.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import com.dynamicdriller.velox.db.BiCycle
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


    val runs = MediatorLiveData<List<BiCycle>>()

    var sortType = SortType.DATE

    init {
        runs.addSource(runSortedByDate){result->
            if (sortType==SortType.DATE){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByAvgSpeedInKmh){result->
            if (sortType==SortType.AVG_SPEED){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByCalories){result->
            if (sortType==SortType.CALORIES_BURNED){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByDistance){result->
            if (sortType==SortType.DISTANCE){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByTimeInMillis){result->
            if (sortType==SortType.RUNNING_TIME){
                result?.let { runs.value = it }
            }
        }
    }


    fun sortRuns(sortType: SortType)= when(sortType){
        SortType.DATE -> runSortedByDate.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runSortedByTimeInMillis.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runSortedByCalories.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runSortedByAvgSpeedInKmh.value?.let { runs.value = it }
        SortType.DISTANCE -> runSortedByDistance.value?.let { runs.value = it }
        else -> {}
    }.also { this.sortType = sortType }



    fun insertBiCycle(bicycle:BiCycle) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.insertBiCycle(bicycle)
    }

}