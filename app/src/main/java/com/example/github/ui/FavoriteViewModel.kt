package com.example.github.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.github.data.database.AppDatabase
import com.example.github.data.database.FavoriteUser
import com.example.github.data.database.FavoriteUserDao

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private val favoriteUserDao: FavoriteUserDao = AppDatabase.getDatabase(application).favoriteUserDao()

    fun getFavoriteUsers(): LiveData<List<FavoriteUser>> = favoriteUserDao.getFavoriteUsers()
}