package com.example.github.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao

interface FavoriteUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: FavoriteUser)

    @Query("SELECT * FROM favorite_user WHERE username = :username")
    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUser>

    @Delete
    suspend fun delete(user: FavoriteUser)

    @Query("SELECT * FROM favorite_user")
    fun getFavoriteUsers(): LiveData<List<FavoriteUser>>
}