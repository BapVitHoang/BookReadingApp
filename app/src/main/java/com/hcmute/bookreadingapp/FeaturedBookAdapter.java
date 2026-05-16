package com.hcmute.bookreadingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeaturedBookAdapter extends RecyclerView.Adapter<FeaturedBookAdapter.BookViewHolder> {

    private final List<FeaturedBook> books;

    public FeaturedBookAdapter(List<FeaturedBook> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_featured_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        FeaturedBook book = books.get(position);
        holder.imgCover.setImageResource(book.getCoverResId());
        holder.imgCover.setContentDescription(
                holder.itemView.getContext().getString(book.getContentDescriptionResId()));
        holder.txtTitle.setText(holder.itemView.getContext().getString(book.getTitleResId()));

        if (book.isBestseller()) {
            holder.txtBadge.setVisibility(View.VISIBLE);
        } else {
            holder.txtBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgCover;
        final TextView txtBadge;
        final TextView txtTitle;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.img_cover);
            txtBadge = itemView.findViewById(R.id.txt_badge);
            txtTitle = itemView.findViewById(R.id.txt_title);
        }
    }
}
