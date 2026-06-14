package com.hcmute.bookreadingapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.controller.LibraryController;
import com.hcmute.bookreadingapp.model.Book;
import com.hcmute.bookreadingapp.model.LibraryBookEntry;
import com.hcmute.bookreadingapp.view.activity.AudioPlayerActivity;
import com.hcmute.bookreadingapp.view.activity.ReadingActivity;
import com.hcmute.bookreadingapp.view.adapter.LibraryBookAdapter;

public class LibraryFragment extends Fragment {

    private LibraryController libraryController;

    private View cardRecent;
    private TextView tvRecentTitle;
    private TextView tvRecentProgress;
    private ProgressBar progressRecent;
    private MaterialButton btnRecentRead;
    private MaterialButton btnRecentListen;

    private TextView tvEmptyLibrary;
    private TextView tvInProgressHeader;
    private TextView tvFavoritesHeader;
    private TextView tvFavoritesEmpty;
    private RecyclerView rvInProgress;
    private RecyclerView rvFavorites;

    private LibraryBookAdapter inProgressAdapter;
    private LibraryBookAdapter favoritesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        libraryController = new LibraryController(requireContext());
        bindViews(view);
        setupRecyclerViews();
        loadLibrary();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLibrary();
    }

    private void bindViews(View view) {
        cardRecent = view.findViewById(R.id.card_recent);
        tvRecentTitle = view.findViewById(R.id.tv_recent_title);
        tvRecentProgress = view.findViewById(R.id.tv_recent_progress);
        progressRecent = view.findViewById(R.id.progress_recent);
        btnRecentRead = view.findViewById(R.id.btn_recent_read);
        btnRecentListen = view.findViewById(R.id.btn_recent_listen);

        tvEmptyLibrary = view.findViewById(R.id.tv_empty_library);
        tvInProgressHeader = view.findViewById(R.id.tv_in_progress_header);
        tvFavoritesHeader = view.findViewById(R.id.tv_favorites_header);
        tvFavoritesEmpty = view.findViewById(R.id.tv_favorites_empty);
        rvInProgress = view.findViewById(R.id.rv_in_progress);
        rvFavorites = view.findViewById(R.id.rv_favorites);
    }

    private void setupRecyclerViews() {
        LibraryBookAdapter.Listener listener = new LibraryBookAdapter.Listener() {
            @Override
            public void onContinueReading(LibraryBookEntry entry) {
                openReading(entry);
            }

            @Override
            public void onContinueListening(LibraryBookEntry entry) {
                openListening(entry);
            }
        };

        inProgressAdapter = new LibraryBookAdapter(listener, true);
        favoritesAdapter = new LibraryBookAdapter(listener, false);

        rvInProgress.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvInProgress.setAdapter(inProgressAdapter);

        rvFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFavorites.setAdapter(favoritesAdapter);
    }

    private void loadLibrary() {
        libraryController.loadLibraryData(new LibraryController.LibraryDataCallback() {
            @Override
            public void onSuccess(LibraryController.LibraryData data) {
                if (!isAdded()) {
                    return;
                }
                bindRecentBook(data.getRecentBook());
                bindInProgressBooks(data.getInProgressBooks());
                bindFavoriteBooks(data.getFavoriteBooks());
                updateEmptyState(data);
            }

            @Override
            public void onError(String message) {
                if (isAdded()) {
                    Toast.makeText(
                            requireContext(),
                            message != null ? message : getString(R.string.library_load_error),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    private void bindRecentBook(LibraryBookEntry entry) {
        if (entry == null) {
            cardRecent.setVisibility(View.GONE);
            return;
        }

        cardRecent.setVisibility(View.VISIBLE);
        Book book = entry.getBook();
        tvRecentTitle.setText(book.getTitle() != null ? book.getTitle() : "");
        progressRecent.setProgress(entry.getProgress());
        tvRecentProgress.setText(getString(R.string.library_reading_percent, entry.getProgress()));

        btnRecentRead.setOnClickListener(v -> openReading(entry));

        if (entry.hasAudio()) {
            btnRecentListen.setVisibility(View.VISIBLE);
            btnRecentListen.setOnClickListener(v -> openListening(entry));
        } else {
            btnRecentListen.setVisibility(View.GONE);
        }
    }

    private void bindInProgressBooks(java.util.List<LibraryBookEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            tvInProgressHeader.setVisibility(View.GONE);
            rvInProgress.setVisibility(View.GONE);
            inProgressAdapter.setEntries(java.util.Collections.emptyList());
            return;
        }

        tvInProgressHeader.setVisibility(View.VISIBLE);
        rvInProgress.setVisibility(View.VISIBLE);
        inProgressAdapter.setEntries(entries);
    }

    private void bindFavoriteBooks(java.util.List<LibraryBookEntry> entries) {
        tvFavoritesHeader.setVisibility(View.VISIBLE);

        if (entries == null || entries.isEmpty()) {
            rvFavorites.setVisibility(View.GONE);
            tvFavoritesEmpty.setVisibility(View.VISIBLE);
            favoritesAdapter.setEntries(java.util.Collections.emptyList());
            return;
        }

        rvFavorites.setVisibility(View.VISIBLE);
        tvFavoritesEmpty.setVisibility(View.GONE);
        favoritesAdapter.setEntries(entries);
    }

    private void updateEmptyState(LibraryController.LibraryData data) {
        boolean hasContent = data.getRecentBook() != null
                || !data.getInProgressBooks().isEmpty()
                || !data.getFavoriteBooks().isEmpty();

        tvEmptyLibrary.setVisibility(hasContent ? View.GONE : View.VISIBLE);
    }

    private void openReading(LibraryBookEntry entry) {
        if (entry == null || entry.getBook() == null) {
            return;
        }
        Intent intent = new Intent(requireContext(), ReadingActivity.class);
        intent.putExtra(ReadingActivity.EXTRA_BOOK, entry.getBook());
        startActivity(intent);
    }

    private void openListening(LibraryBookEntry entry) {
        if (entry == null || !entry.hasAudio()) {
            return;
        }
        Book book = entry.getBook();
        Intent intent = new Intent(requireContext(), AudioPlayerActivity.class);
        intent.putExtra(AudioPlayerActivity.EXTRA_AUDIO_URL, entry.getAudioUrl());
        intent.putExtra(AudioPlayerActivity.EXTRA_TITLE, book.getTitle());
        if (book.getCoverUrl() != null) {
            intent.putExtra(AudioPlayerActivity.EXTRA_COVER_URL, book.getCoverUrl());
        }
        startActivity(intent);
    }
}
