package com.hcmute.bookreadingapp.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.model.FeaturedBook;

import java.util.ArrayList;
import java.util.List;

public class FeaturedBookAdapter extends RecyclerView.Adapter<FeaturedBookAdapter.FeaturedViewHolder> {

    private List<FeaturedBook> featuredBooks = new ArrayList<>();
    private final OnFeaturedBookListener listener;

    public interface OnFeaturedBookListener {
        void onPlayClick(FeaturedBook book);

        void onCardClick(FeaturedBook book);
    }

    public FeaturedBookAdapter(OnFeaturedBookListener listener) {
        this.listener = listener;
    }

    public void setFeaturedBooks(List<FeaturedBook> books) {
        this.featuredBooks = books;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_featured_book, parent, false);
        return new FeaturedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
        FeaturedBook book = featuredBooks.get(position);

        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(book.getCoverUrl())
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.imgCover);
        } else {
            holder.imgCover.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.layoutBestSeller.setVisibility(book.isBestSeller() ? View.VISIBLE : View.GONE);

        holder.btnPlay.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayClick(book);
            }
        });

        View.OnClickListener openDetailListener = v -> {
            if (listener != null) {
                listener.onCardClick(book);
            }
        };
        holder.cardFeatured.setOnClickListener(openDetailListener);
        holder.imgCover.setOnClickListener(openDetailListener);
    }

    @Override
    public int getItemCount() {
        return featuredBooks.size();
    }

    static class FeaturedViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardFeatured;
        ImageView imgCover;
        LinearLayout layoutBestSeller;
        FloatingActionButton btnPlay;

        FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);
            cardFeatured = itemView.findViewById(R.id.card_featured_book);
            imgCover = itemView.findViewById(R.id.img_featured_cover);
            layoutBestSeller = itemView.findViewById(R.id.layout_best_seller);
            btnPlay = itemView.findViewById(R.id.btn_play_featured);
        }
    }
}
