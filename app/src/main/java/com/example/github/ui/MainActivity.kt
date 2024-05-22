package com.example.github

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.github.databinding.ActivityMainBinding
import com.example.github.ui.FavoriteActivity
import com.example.github.ui.MainViewModel
import com.example.github.ui.SettingPreferences
import com.example.github.ui.SettingViewModel
import com.example.github.ui.SettingViewModelFactory
import com.example.github.ui.SettingsActivity
import com.example.github.ui.UserAdapter
import com.example.github.ui.dataStore
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UserAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Github Search User"

        // Mengatur RecyclerView dan pengamat
        setupRecyclerView()
        setupObservers()
        setupSearchView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu) // Inflate menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                // Aksi ketika item-menu Favorite diklik
                startActivity(Intent(this, FavoriteActivity::class.java))
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

    private fun setupRecyclerView() {
        // Konfigurasi RecyclerView
        val layoutManager = LinearLayoutManager(this)
        binding.rvUserList.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUserList.addItemDecoration(itemDecoration)

        // Inisialisasi adapter
        adapter = UserAdapter()
        binding.rvUserList.adapter = adapter
    }

    private fun setupObservers() {
        // Observasi LiveData
        viewModel.loadingLiveData.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.usersLiveData.observe(this) { users ->
            adapter.submitList(users)
        }

        viewModel.errorLiveData.observe(this) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }
        val pref = SettingPreferences.getInstance(application.dataStore)
        val settingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref)).get(
            SettingViewModel::class.java)
        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchView.text.toString().trim()
            viewModel.fetchUsers(query)
            binding.searchView.hide()
            Snackbar.make(binding.root, query, Snackbar.LENGTH_SHORT).show()
            false
        }
    }

    private fun showLoading(isLoading: Boolean) {
        // Menampilkan atau menyembunyikan ProgressBar
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}