package com.example.criminalintent

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.PhotosAdapter
import com.example.criminalintent.R
import com.example.criminalintent.db.IntentDataBase

class SavedPhotosActivity : AppCompatActivity() {

    lateinit var photosAdapter: PhotosAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)

        val recyclerView: RecyclerView = findViewById(R.id.photos_list)
        photosAdapter = PhotosAdapter(emptyList())
        recyclerView.adapter = photosAdapter

        // Запросите данные из базы данных Room и установите их в адаптере
        val photosDao = IntentDataBase.getInstance(this).getIntentDao()
        photosDao.getAllIntents().observe(this) { photos ->
            photosAdapter.photos = photos
            photosAdapter.notifyDataSetChanged()
        }
    }
}