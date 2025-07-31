package com.example.phychosiolz.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.phychosiolz.data.room.model.TestData
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(testData: TestData)

    @Query(
        "SELECT * FROM test_data WHERE uid = :uid AND year = :year AND month = :month " +
                "AND day = :day AND period = :period AND type = :type"
    )
    fun getTestData(uid: Int, year: Int, month: Int, day: Int, period: Int, type: Int): TestData?

    @Update
    fun update(testData: TestData)

    @Query(
        "SELECT * FROM test_data WHERE uid = :uid AND year = :year AND month = :month " +
                "AND day = :day AND type = :type"
    )
    fun getTestDataListInDay(uid: Int, year: Int, month: Int, day: Int, type: Int): Flow<List<TestData>?>
}