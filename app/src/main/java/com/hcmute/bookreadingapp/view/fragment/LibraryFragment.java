package com.hcmute.bookreadingapp.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.hcmute.bookreadingapp.R;
import com.hcmute.bookreadingapp.controller.LibraryController;

public class LibraryFragment extends Fragment {

    private TextView tvRecentlyOpened;
    private LibraryController libraryController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        libraryController = new LibraryController(requireContext());
        tvRecentlyOpened = view.findViewById(R.id.tv_recently_opened);

        refreshRecentlyOpened();

        return view;
    }

    private void refreshRecentlyOpened() {
        tvRecentlyOpened.setText(libraryController.getRecentlyOpenedLabel());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tvRecentlyOpened != null) {
            refreshRecentlyOpened();
        }
    }
}
