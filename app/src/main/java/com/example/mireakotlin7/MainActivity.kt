package com.example.mireakotlin7

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var urlInput: EditText
    private lateinit var downloadButton: Button
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        urlInput = findViewById(R.id.urlInput)
        downloadButton = findViewById(R.id.downloadButton)
        imageView = findViewById(R.id.imageView)

        downloadButton.setOnClickListener {
            val imageUrl = urlInput.text.toString()
            if (imageUrl.isNotEmpty()) {
                downloadAndSaveImage(imageUrl)
            }
        }
    }

    private fun downloadAndSaveImage(url: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Загрузка изображения
                val bitmap = withContext(Dispatchers.IO) { downloadImage(url) }

                // Сохранение изображения
                withContext(Dispatchers.IO) { saveImageToInternalStorage(bitmap) }

                // Отображение изображения на экране
                displayImageFromInternalStorage()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun downloadImage(url: String): Bitmap {
        val imageUrl = URL(url)
        val connection = imageUrl.openConnection()
        connection.doInput = true
        connection.connect()
        val inputStream = connection.getInputStream()
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap) {
        val filename = "downloaded_image.png"
        openFileOutput(filename, MODE_PRIVATE).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
    }

    private fun displayImageFromInternalStorage() {
        val file = File(filesDir, "downloaded_image.png")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            imageView.setImageBitmap(bitmap)
        }
    }
}