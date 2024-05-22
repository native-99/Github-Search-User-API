package com.example.github

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.github.data.database.FavoriteUser
import com.example.github.data.response.DetailUserResponse
import com.example.github.databinding.ActivityDetailBinding
import com.example.github.ui.FavoriteActivity
import com.example.github.ui.SectionsPagerAdapter
import com.example.github.ui.SettingsActivity
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()

    private var currentUserDetail: DetailUserResponse? = null
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail User"

        // Handle back button press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        // Mendapatkan username dari intent
        val username = intent.getStringExtra(ARG_USERNAME) ?: ""

        // Inisialisasi ViewPager dan TabLayout
        val viewPager = binding.viewPager
        val tabLayout = binding.tabs

        // Buat adapter untuk ViewPager
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        sectionsPagerAdapter.username = username
        viewPager.adapter = sectionsPagerAdapter

        // Hubungkan ViewPager dengan TabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Followers"
                else -> "Following"
            }
        }.attach()

        // Panggil fungsi untuk mengambil detail pengguna
        viewModel.getDetailUser(username)

        // Observasi data detail pengguna
        viewModel.detailUserLiveData.observe(this) { detailUser ->
            currentUserDetail = detailUser
            displayUserDetail(detailUser)
        }

        // Observasi indikator loading
        viewModel.loadingLiveData.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observasi status favorite user
        viewModel.getFavoriteUserByUsername(username).observe(this) { user ->
            isFavorite = user != null
            updateFavoriteIcon()
        }

        // Set onClickListener pada FAB
        binding.fabFavorite.setOnClickListener {
            val user = FavoriteUser(username = username, avatarUrl = currentUserDetail?.avatarUrl)
            if (isFavorite) {
                viewModel.delete(user)
                showToastMessage("$username dihapus dari favorit")
            } else {
                viewModel.insert(user)
                showToastMessage("$username disimpan ke favorit")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.fabFavorite.setImageResource(R.drawable.baseline_favorite_24)
        } else {
            binding.fabFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
        }
    }

    private fun displayUserDetail(detailUser: DetailUserResponse) {
        binding.apply {
            // Menampilkan gambar profil menggunakan Glide
            Glide.with(this@DetailActivity)
                .load(detailUser.avatarUrl)
                .into(imgAvatar)

            tvName.text = detailUser.name
            tvUsername.text = detailUser.login ?: ""
            tvLocation.text = detailUser.location
            tvCompany.text = detailUser.company
            tvRepository.text = detailUser.publicRepos.toString()
            tvFollowers.text = detailUser.followers.toString()
            tvFollowing.text = detailUser.following.toString()
        }
    }
    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    companion object {
        const val ARG_USERNAME = "username"
    }
}

