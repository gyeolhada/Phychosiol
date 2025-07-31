package com.example.phychosiolz.repo

import androidx.lifecycle.MutableLiveData
import com.example.phychosiolz.data.room.dao.AttackDao
import com.example.phychosiolz.data.room.model.Attack
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Date

class UserAttackRepository(
    private val _userRepo: UserRepository,
    private val _attackDao: AttackDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    fun saveAttack(startTime:String,endTime:String,content:String) {
        val uid = _userRepo.getCurrentUserID()!!
        val attack = Attack(
            null, startTime,endTime,content,uid
        )
        _attackDao.insert(attack)
    }

   fun getAllAttacksForUser(): List<Attack>{
        return _attackDao.getAllAttacksByUid(_userRepo.getCurrentUserID()!!)
    }
}