package com.example.mlkit_document_scanner;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mlkit_document_scanner.adapters.DocumentListAdapter;
import com.example.mlkit_document_scanner.databinding.ActivityMainBinding;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List<Uri> documentUris = new ArrayList<>();
    private DocumentListAdapter documentListAdapter;

    private final androidx.activity.result.ActivityResultLauncher<IntentSenderRequest> scannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
    this::handleActivityResult
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        documentListAdapter = new DocumentListAdapter(this, documentUris);
        binding.documentListView.setLayoutManager(new LinearLayoutManager(this));
        binding.documentListView.setAdapter(documentListAdapter);

        setupActions();
    }

    private void setupActions() {
        binding.scanButton.setOnClickListener(v -> scanDocument());
    }

    private void scanDocument() {
        try {
            GmsDocumentScannerOptions options = new GmsDocumentScannerOptions.Builder()
                .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
                .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF, GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
                .setGalleryImportAllowed(false)
                .setPageLimit(3)
                .build();

            GmsDocumentScanning.getClient(options)
                .getStartScanIntent(this)
                .addOnSuccessListener(intentSender -> {
                IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender).build();
                scannerLauncher.launch(request);
            })
            .addOnFailureListener(e -> Log.e("MainActivity", "Scanning failed: " + e.getMessage(), e));

        } catch (Exception e) {
            Log.e("MainActivity", "Exception during document scanning: " + e.getMessage(), e);
        }
    }

    private void handleActivityResult(ActivityResult activityResult) {
        try {
            int resultCode = activityResult.getResultCode();
            GmsDocumentScanningResult result = GmsDocumentScanningResult.fromActivityResultIntent(activityResult.getData());

            if (resultCode == RESULT_OK && result != null) {
                Uri firstPageUri = result.getPages().get(0).getImageUri();

                if (firstPageUri != null) {
                    documentUris.add(firstPageUri);
                    documentListAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error handling activity result: " + e.getMessage(), e);
        }
    }
}
