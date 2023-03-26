package com.example.criminalintent

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.criminalintent.data.Model.Photos
import com.example.criminalintent.data.repository.Repository
import com.example.criminalintent.databinding.ActivityMainBinding
import com.example.criminalintent.db.IntentDataBase
import com.example.criminalintent.db.repository.IntentRealization
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var repo = Repository()
    var photoList: MutableLiveData<Response<Photos>> = MutableLiveData()
    private val adapter = IntentListAdapter()
    private var fragment =  IntentFragment.newInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        APP = this
        init()
        initDataBase()
        val flickrCheckerRequest = PeriodicWorkRequestBuilder<FlickrChecker>(30, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueue(flickrCheckerRequest)
        val search = findViewById<TextInputEditText>(R.id.search)

        search.doOnTextChanged { text, start, count, after ->
            if (text!!.isEmpty()) {
                init()
            } else {
                outputPhotos(text.toString())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                val intent = Intent(this, SavedPhotosActivity::class.java)
                startActivity(intent)
            }
            R.id.delete -> {
                val db = IntentDataBase.getInstance(this).getIntentDao()
                GlobalScope.launch {
                    db.deleteAll()
                }
            }
        }
        return true
    }

    private fun initDataBase() {
        val daoIntent = IntentDataBase.getInstance(this).getIntentDao()
        REPOSITORY = IntentRealization(daoIntent)
    }

    fun getPhotos() {
        lifecycleScope.launch {
            photoList.value = repo.getPhotos()
        }
    }

    fun searchPhotos(tag: String) {
        lifecycleScope.launch {
            photoList.value = repo.getTagsPhotos(tag)
        }
    }



    fun outputPhotos(tag: String) {
        binding.apply {
            rcView.layoutManager = GridLayoutManager(this@MainActivity, 3)
            rcView.adapter = adapter
            searchPhotos(tag)
            photoList.observe(this@MainActivity) {  list ->
                list.body()?.photos?.let { adapter.setList(it.photo) }
            }
        }
    }

    private fun init() {
        binding.apply {
            rcView.layoutManager = GridLayoutManager(this@MainActivity, 3)
            rcView.adapter = adapter
            getPhotos()
            photoList.observe(this@MainActivity) {  list ->
                list.body()?.photos?.let { adapter.setList(it.photo) }
            }
        }
    }
}
