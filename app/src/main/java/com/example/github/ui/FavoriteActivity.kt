package com.example.github.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.github.R
import com.example.github.data.response.ItemsItem
import com.example.github.databinding.ActivityFavoriteBinding


class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: UserAdapter // Asumsikan adapter sudah dibuat

    // Inisialisasi ViewModel menggunakan viewModels delegate
    private val viewModel: FavoriteViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Favorite Users"

        // Inisialisasi RecyclerView dan adapter
        adapter = UserAdapter()
        binding.rvUserList.adapter = adapter
        binding.rvUserList.layoutManager = LinearLayoutManager(this)

        // Observasi data dari ViewModel
        viewModel.getFavoriteUsers().observe(this, Observer { users ->
            binding.progressBar.visibility = View.GONE
            val items = users.map {
                ItemsItem(login = it.username, avatarUrl = it.avatarUrl)
            }
            adapter.submitList(items)
        })

        // Handle back button press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.action_setting -> {
                // Aksi ketika item-menu Settings diklik
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.favorite_menu, menu)
        return true
    }
}