package com.example.imagepicker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SecondActivity : AppCompatActivity() {
    private lateinit var selectMultipleImagesButton: Button
    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var multipleImagesLauncher: ActivityResultLauncher<Intent>
    private val imageUris = ArrayList<Uri>() // Change to ArrayList for better compatibility with the adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        multipleImagesLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val clipData = result.data?.clipData
                val singleImage = result.data?.data
                imageUris.clear()
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri
                        imageUris.add(imageUri)
                    }
                    imagesAdapter.notifyItemRangeInserted(0, clipData.itemCount)
                } else if (singleImage != null) {
                    imageUris.add(singleImage)
                    imagesAdapter.notifyItemInserted(0)
                }
                updateRecyclerViewVisibility()
            }
        }

        selectMultipleImagesButton = findViewById(R.id.selectMultipleImagesButton)
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView)

        selectMultipleImagesButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                type = "image/*"
            }
            multipleImagesLauncher.launch(intent)
        }

        imagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imagesAdapter = ImagesAdapter(imageUris) { position ->
            imagesAdapter.removeItem(position)
            updateRecyclerViewVisibility()
        }
        imagesRecyclerView.adapter = imagesAdapter

        updateRecyclerViewVisibility()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateRecyclerViewVisibility() {
        imagesRecyclerView.visibility = if (imageUris.isEmpty()) RecyclerView.GONE else RecyclerView.VISIBLE
    }
}