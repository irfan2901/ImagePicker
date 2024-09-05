package com.example.imagepicker

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var nextButton: Button
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        selectImageButton = findViewById(R.id.selectImageButton)
        nextButton = findViewById(R.id.nextButton)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    openGallery()
                } else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show()
                }
            }

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data.let { uri ->
                        imageView.setImageURI(uri)
                    }
                }
            }

        selectImageButton.setOnClickListener {
            checkForPermission()
        }

        nextButton.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    private fun checkForPermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // For Android 11 and above
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openGallery()
                } else {
                    // Prompt user to grant MANAGE_EXTERNAL_STORAGE permission
                    Toast.makeText(
                        this,
                        "Please grant Manage External Storage permission",
                        Toast.LENGTH_LONG
                    ).show()
                    // You might need to redirect the user to app settings for manual permission grant
                    val intent =
                        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                            data = android.net.Uri.parse("package:${packageName}")
                        }
                    startActivity(intent)
                }
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // For Android 10
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openGallery()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

            else -> {
                // For Android 6.0 - 9.0
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openGallery()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }
}