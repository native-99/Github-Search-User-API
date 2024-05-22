package com.example.github.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.github.data.response.GithubResponse
import com.example.github.data.response.ItemsItem
import com.example.github.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _usersLiveData: MutableLiveData<List<ItemsItem>> = MutableLiveData()
    val usersLiveData: LiveData<List<ItemsItem>> = _usersLiveData

    private val _errorLiveData: MutableLiveData<String> = MutableLiveData()
    val errorLiveData: LiveData<String> = _errorLiveData

    private val _loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val loadingLiveData: LiveData<Boolean> = _loadingLiveData

    init {
        // Panggil metode fetchUsers() tanpa parameter saat ViewModel dibuat
        fetchUsers()
    }

    fun fetchUsers(query: String? = null) {
        _loadingLiveData.postValue(true) // Set loading menjadi true saat permintaan dimulai

        ApiConfig.getApiService().getListUsers(query ?: "").enqueue(object : Callback<GithubResponse> {
            override fun onResponse(call: Call<GithubResponse>, response: Response<GithubResponse>) {
                _loadingLiveData.postValue(false) // Set loading menjadi false saat permintaan selesai
                if (response.isSuccessful) {
                    val githubResponse = response.body()
                    if (githubResponse != null) { // Periksa apakah respons tidak null
                        val users = githubResponse.items ?: emptyList()
                        _usersLiveData.postValue(users as List<ItemsItem>?)
                    } else {
                        _errorLiveData.postValue("Response body is null")
                    }
                } else {
                    _errorLiveData.postValue("Failed to fetch users: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                _loadingLiveData.postValue(false) // Set loading menjadi false saat permintaan gagal
                _errorLiveData.postValue("Failed to fetch users")
            }
        })
    }
}
