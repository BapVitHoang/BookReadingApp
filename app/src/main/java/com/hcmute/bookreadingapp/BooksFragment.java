package com.hcmute.bookreadingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;

public class BooksFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);

        LinearLayout bookItem = view.findViewById(R.id.layout_book_item);
        MaterialCardView featuredCard = view.findViewById(R.id.card_featured);

        bookItem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BookDetailActivity.class);
            startActivity(intent);
        });

        featuredCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AudioPlayerActivity.class);
            startActivity(intent);
        });

        return view;
    }
}