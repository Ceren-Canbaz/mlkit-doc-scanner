package com.example.mlkit_document_scanner.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mlkit_document_scanner.R;

import java.util.List;

public class DocumentListAdapter extends RecyclerView.Adapter<DocumentListAdapter.ViewHolder> {

    private final Context context;
    private final List<Uri> documentUris;

    public DocumentListAdapter(Context context, List<Uri> documentUris) {
        this.context = context;
        this.documentUris = documentUris;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView documentImageView;
        public TextView documentTextView;

        public ViewHolder(View view) {
            super(view);
            documentImageView = view.findViewById(R.id.documentImageView);
            documentTextView = view.findViewById(R.id.documentTextView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_document, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri documentUri = documentUris.get(position);
        holder.documentImageView.setImageURI(documentUri);
        holder.documentTextView.setText(documentUri.getLastPathSegment());
    }

    @Override
    public int getItemCount() {
        return documentUris.size();
    }
}
