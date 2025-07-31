package com.example.phychosiolz.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.phychosiolz.MyApplication
import com.example.phychosiolz.data.enums.EmotionType
import com.example.phychosiolz.repo.EmotionAndDiaryRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmotionEditViewModel(private val _repository: EmotionAndDiaryRepository) : ViewModel() {
    val title by lazy { MutableLiveData<String>() }
    val content by lazy { MutableLiveData<String>() }
    val emotion by lazy { MutableLiveData<EmotionType>() }
    val images by lazy { MutableLiveData<MutableList<String>>() }

    init {
        images.value = mutableListOf()
        content.value = ""
        title.value = ""
        emotion.value = EmotionType.NEUTRAL
    }

    //异常处理,处理协程中的异常
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("EmotionEditViewModel", "exceptionHandler: $throwable")
    }

    //保存日记,使用协程。
    //在view层进行数据的检查,如果数据不合法,则不进行保存。这里不进行数据的检查,只进行数据的保存。
    fun saveDiary(onSuccess: () -> Unit, onFail: () -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            try {
                withContext(Dispatchers.IO) {
                    _repository.saveDiary(
                        title.value!!,
                        content.value!!,
                        emotion.value!!.code,
                        images.value!!
                    )
                }
                onSuccess()
            }catch (e: Exception) {
                onFail()
            }
        }
    }

    fun submitImages(toMutableList: MutableList<String>,
                     onTooMuch: () -> Unit
                     ) {
        val oriSize = images.value!!.size
        //set max to MAX_IMAGE_SIZE
        if (oriSize + toMutableList.size > MAX_IMAGE_SIZE) {
            onTooMuch()
        }
        if (oriSize + toMutableList.size > MAX_IMAGE_SIZE) {
            toMutableList.subList(
                MAX_IMAGE_SIZE - oriSize,
                toMutableList.size
            ).clear()
        }
        images.value!!.addAll(toMutableList)
        images.value = images.value
    }

    fun removeImage(it: Int) {
        images.value!!.removeAt(it)
        images.value = images.value

    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return EmotionEditViewModel(
                    (application as MyApplication).emotionAndDiaryRepository
                ) as T
            }
        }

        const val MAX_IMAGE_SIZE = 9
    }
}