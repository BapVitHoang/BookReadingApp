package com.hcmute.bookreadingapp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.model.Book;
import com.hcmute.bookreadingapp.model.LibraryBookEntry;

import java.util.ArrayList;
import java.util.List;

public class LibraryBookAdapter extends RecyclerView.Adapter<LibraryBookAdapter.LibraryBookViewHolder> {

    public interface Listener {
        void onContinueReading(LibraryBookEntry entry);

        void onContinueListening(LibraryBookEntry entry);
    }

    private final Listener listener;
    private final List<LibraryBookEntry> entries = new ArrayList<>();
    private final boolean showProgress;

    public LibraryBookAdapter(Listener listener, boolean showProgress) {
        this.listener = listener;
        this.showProgress = showProgress;
    }

    public void setEntries(List<LibraryBookEntry> items) {
        entries.clear();
        if (items != null) {
            entries.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LibraryBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_library_book, parent, false);
        return new LibraryBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryBookViewHolder holder, int position) {
        LibraryBookEntry entry = entries.get(position);
        Book book = entry.getBook();

        holder.tvTitle.setText(book.getTitle() != null ? book.getTitle() : "");
        holder.tvAuthor.setText(book.getAuthor() != null ? book.getAuthor() : "");

        if (showProgress) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.tvProgress.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress(entry.getProgress());
            holder.tvProgress.setText(holder.itemView.getContext().getString(
                    R.string.library_reading_percent, entry.getProgress()));
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.tvProgress.setVisibility(View.GONE);
        }

        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(book.getCoverUrl())
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.imgCover);
        } else {
            holder.imgCover.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.btnContinueReading.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContinueReading(entry);
            }
        });

        if (entry.hasAudio()) {
            holder.btnContinueListening.setVisibility(View.VISIBLE);
            holder.btnContinueListening.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onContinueListening(entry);
                }
            });
        } else {
            holder.btnContinueListening.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class LibraryBookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvTitle;
        TextView tvAuthor;
        ProgressBar progressBar;
        TextView tvProgress;
        MaterialButton btnContinueReading;
        MaterialButton btnContinueListening;

        LibraryBookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.img_library_cover);
            tvTitle = itemView.findViewById(R.id.tv_library_title);
            tvAuthor = itemView.findViewById(R.id.tv_library_author);
            progressBar = itemView.findViewById(R.id.progress_reading);
            tvProgress = itemView.findViewById(R.id.tv_library_progress);
            btnContinueReading = itemView.findViewById(R.id.btn_continue_reading);
            btnContinueListening = itemView.findViewById(R.id.btn_continue_listening);
        }
    }
}
