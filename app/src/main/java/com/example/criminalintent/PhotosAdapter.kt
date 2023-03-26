package com.example.criminalintent

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.databinding.ActivityMainBinding
import com.example.criminalintent.databinding.PhotoItemBinding
import com.squareup.picasso.Picasso

class PhotosAdapter(var photos: List<IntentModel>) : RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(private val binding: PhotoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photos: IntentModel) {
            binding.photoTitle.text = photos.title
            Picasso.get()
                .load(photos.url_s)
                .placeholder(R.drawable.sample_bitmap)
                .resize(250, 250)
                .into(binding.photoImage)
            binding.photoDescription.text = photos.url_s
            binding.photoOwner.text = photos.owner
            binding.photoTags.text = photos.secret
        }
        /*val image: ImageView = itemView.findViewById(R.id.photo_image)
        val title: TextView = itemView.findViewById(R.id.photo_title)
        val description: TextView = itemView.findViewById(R.id.photo_description)
        val owner: TextView = itemView.findViewById(R.id.photo_owner)
        val tags: TextView = itemView.findViewById(R.id.photo_tags)*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        holder.bind(photo)
        /*val photo = photos[position]
        Picasso.get()
            .load(photo.url_s)
            .placeholder(R.drawable.sample_bitmap)
            .resize(250, 250)
            .into(holder.image)
        holder.title.text = photo.title
        holder.description.text = "none"
        holder.owner.text = photo.owner
        holder.tags.text = "none"*/
    }

    override fun getItemCount(): Int {
        return photos.size
    }
}
