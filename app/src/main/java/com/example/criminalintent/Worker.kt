package com.example.criminalintent

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.Settings.System.getString
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.criminalintent.data.Model.Photo
import com.example.criminalintent.data.Model.Photos
import com.example.criminalintent.data.api.ApiService
import com.example.criminalintent.data.repository.Repository
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class FlickrChecker(private val context: Context, params: WorkerParameters)
    : Worker(context, params) {

    override fun doWork(): Result {
        val photoList = Repository().getRecentPhotos()
        photoList.enqueue(object : Callback<Photos> {
            override fun onResponse(call: Call<Photos>, response: Response<Photos>) {
                if (response.isSuccessful) {
                    val data = response.body()!!.photos.photo
                    if (data.isNotEmpty()) {
                        // Создание канала уведомления
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val name = "Канал уведомлений"
                            val importance = NotificationManager.IMPORTANCE_DEFAULT
                            val channel = NotificationChannel("my_channel_01", name, importance).apply {
                                description = "Описание канала уведомлений"
                            }
                            val notificationManager: NotificationManager =
                                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.createNotificationChannel(channel)
                        }

                        // Создание объекта уведомления
                        val notification = NotificationCompat.Builder(applicationContext, "my_channel_01")
                            .setContentTitle("Новые фото")
                            .setContentText("На flickr появились новые фото")
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .build()

                        // Отображение уведомления
                        val notificationManager = NotificationManagerCompat.from(applicationContext)
                        notificationManager.notify(0, notification)
                    }
                }
            }

            override fun onFailure(call: Call<Photos>, t: Throwable) {
               Result.failure()
            }
        })




        return Result.success()
    }
}
