package com.example.github

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.github.data.database.AppDatabase
import com.example.github.data.database.FavoriteUser
import com.example.github.data.database.FavoriteUserDao
import com.example.github.data.response.DetailUserResponse
import com.example.github.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class DetailViewModel(application: Application) : AndroidViewModel(application) {

    // API Service initialization
    private val apiService = ApiConfig.getApiService()

    private val _detailUserLiveData: MutableLiveData<DetailUserResponse> = MutableLiveData()
    val detailUserLiveData: LiveData<DetailUserResponse> = _detailUserLiveData

    private val _errorLiveData: MutableLiveData<String> = MutableLiveData()
    val errorLiveData: LiveData<String> = _errorLiveData

    private val _loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val loadingLiveData: LiveData<Boolean> = _loadingLiveData

    private val dao: FavoriteUserDao = AppDatabase.getDatabase(application).favoriteUserDao()

    fun insert(user: FavoriteUser) = viewModelScope.launch {
        dao.insert(user)
    }

    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUser> {
        return dao.getFavoriteUserByUsername(username)
    }

    fun delete(user: FavoriteUser) = viewModelScope.launch {
        dao.delete(user)
    }

    // API function
    fun getDetailUser(username: String) {
        _loadingLiveData.postValue(true)

        apiService.getDetailUser(username).enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(
                call: Call<DetailUserResponse>,
                response: Response<DetailUserResponse>
            ) {
                _loadingLiveData.postValue(false)
                if (response.isSuccessful) {
                    _detailUserLiveData.postValue(response.body())
                } else {
                    _errorLiveData.postValue("Failed to get user details")
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                _loadingLiveData.postValue(false)
                _errorLiveData.postValue("Failed to fetch user details")
            }
        })
    }
}
