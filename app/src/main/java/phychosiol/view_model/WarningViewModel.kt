package com.example.phychosiolz.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.phychosiolz.MyApplication
import com.example.phychosiolz.data.room.model.Attack
import com.example.phychosiolz.repo.UserAttackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WarningViewModel(private val _repository: UserAttackRepository) : ViewModel() {
    val lastTime = MutableLiveData<Long>(0L)
    fun startCounting() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                while (true) {
                    Thread.sleep(1000)
                    withContext(Dispatchers.Main) {
                        lastTime.value = lastTime.value!! + 1
                    }
                }
            }
        }
    }

    fun refresh() {
        url.value = INTERESTING_URL.random()
    }

    val url = MutableLiveData(
        INTERESTING_URL.random()
    )

   fun saveContent(startTime:String, endTime:String, content: String) {
       _repository.saveAttack(startTime,endTime,content)
    }

   fun getAllAttacks():List<Attack>{
       return  _repository.getAllAttacksForUser()
   }

    companion object {
        val INTERESTING_URL = listOf(
            "https://m.baidu.com/bh/m/detail/ar_4046873170272791722",
        )
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return WarningViewModel(
                    (application as MyApplication).userAttackRepository
                ) as T
            }
        }
    }
}