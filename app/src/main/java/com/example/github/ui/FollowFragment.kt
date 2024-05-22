package com.example.github.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.github.R
import com.example.github.data.response.ItemsItem
import com.example.github.data.retrofit.ApiConfig
import com.example.github.databinding.FragmentFollowBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowFragment : Fragment() {
    private var position: Int = 0
    private var username: String = ""
    private lateinit var adapter: UserAdapter // Assume you have a UserAdapter class
    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!

    // Deklarasi LiveData untuk error dan loading
    private val _errorLiveData: MutableLiveData<String> = MutableLiveData()
    val errorLiveData: LiveData<String> = _errorLiveData

    private val _loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val loadingLiveData: LiveData<Boolean> = _loadingLiveData

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME) ?: ""
        }

        adapter = UserAdapter() // Initialize your adapter here

        binding.rvUserList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FollowFragment.adapter
        }

        // Observasi LiveData untuk loading
        loadingLiveData.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        fetchData(username)
    }

    private fun fetchData(username: String) {
        val apiService = ApiConfig.getApiService()

        _loadingLiveData.postValue(true) // Set loading menjadi true sebelum melakukan permintaan

        val call: Call<List<ItemsItem>> = if (position == 1) {
            apiService.getFollowers(username)
        } else {
            apiService.getFollowing(username)
        }

        call.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(call: Call<List<ItemsItem>>, response: Response<List<ItemsItem>>) {
                _loadingLiveData.postValue(false) // Set loading menjadi false setelah permintaan selesai

                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    adapter.submitList(users)
                } else {
                    // Handle error jika response tidak berhasil
                    _errorLiveData.postValue("Failed to get user details")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                _loadingLiveData.postValue(false) // Set loading menjadi false saat permintaan gagal
                _errorLiveData.postValue("Failed to fetch user details")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_POSITION = "position"
        const val ARG_USERNAME = "username"
    }
}
