package com.example.github.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_user")
data class FavoriteUser(

    @PrimaryKey(autoGenerate = false)
    var username: String = "",
    var avatarUrl: String? = null
)

