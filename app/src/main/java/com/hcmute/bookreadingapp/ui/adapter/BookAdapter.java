package com.hcmute.bookreadingapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.model.Book;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList = new ArrayList<>();
    private final OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public BookAdapter(OnBookClickListener listener) {
        this.listener = listener;
    }

    public void setBookList(List<Book> books) {
        this.bookList = books;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());

        // Load ảnh cover từ URL (internet) bằng Glide
        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(book.getCoverUrl())
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(holder.imgCover);
        } else {
            holder.imgCover.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvTitle;
        TextView tvAuthor;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.img_book_cover);
            tvTitle = itemView.findViewById(R.id.tv_book_title);
            tvAuthor = itemView.findViewById(R.id.tv_book_author);
        }
    }
}
