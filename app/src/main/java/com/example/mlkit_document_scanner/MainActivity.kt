package com.example.mlkit_document_scanner
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.mlkit_document_scanner.databinding.ActivityMainBinding
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val scannerLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        handleActivityResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActions()
    }

    private fun setupActions() {
        binding.scanButton.setOnClickListener {
            scanDocument()
        }
    }

    private fun scanDocument() {
        try {
            val options = GmsDocumentScannerOptions.Builder()
                .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
                .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF, GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
                .setGalleryImportAllowed(false)
                .setPageLimit(3)
                .build()

            GmsDocumentScanning.getClient(options)
                .getStartScanIntent(this)
                .addOnSuccessListener { intentSender: IntentSender ->
                    scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                }
                .addOnFailureListener() { e: Exception ->
                    Log.e("MainActivity", "Scanning failed: ${e.message}", e)
                }

        } catch (e: Exception) {
            Log.e("MainActivity", "Exception during document scanning: ${e.message}", e)
        }
    }

    private fun handleActivityResult(activityResult: ActivityResult) {
        try {
            val resultCode = activityResult.resultCode
            val result = GmsDocumentScanningResult.fromActivityResultIntent(activityResult.data)

            if (resultCode == RESULT_OK && result != null) {
                val firstPageUri = result.pages?.firstOrNull()?.getImageUri()

                if (firstPageUri != null) {
                    showImageInImageView(firstPageUri)
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error handling activity result: ${e.message}", e)
        }
    }

    private fun showImageInImageView(imageUri: Uri) {
        binding.imageView.visibility = View.VISIBLE

        // ImageView ile resmi göster
        binding.imageView.setImageURI(imageUri)
    }




    companion object {
        init {
            System.loadLibrary("mlkit_document_scanner")
        }
    }
}