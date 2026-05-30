package com.hcmute.bookreadingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.hcmute.bookreadingapp.storage.StorageManager;

public class LibraryFragment extends Fragment {

    private TextView tvRecentlyOpened;

    public LibraryFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        tvRecentlyOpened = view.findViewById(R.id.tv_recently_opened);

        loadRecentlyOpenedBook();

        return view;
    }

    private void loadRecentlyOpenedBook() {
        String lastBook = StorageManager.getLastBook(requireContext());

        tvRecentlyOpened.setText("Recently Opened: " + lastBook);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (tvRecentlyOpened != null) {
            loadRecentlyOpenedBook();
        }
    }
}