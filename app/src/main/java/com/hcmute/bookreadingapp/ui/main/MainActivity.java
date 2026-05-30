package com.hcmute.bookreadingapp.ui.main;
import com.hcmute.bookreadingapp.ui.main.ChallengesFragment;
import com.hcmute.bookreadingapp.ui.main.LibraryFragment;
import com.hcmute.bookreadingapp.ui.main.ExploreFragment;
import com.hcmute.bookreadingapp.ui.audio.PodCourseFragment;
import com.hcmute.bookreadingapp.ui.main.BooksFragment;

import com.hcmute.bookreadingapp.R;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        
        // Màn hình mặc định khi vào app
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BooksFragment()).commit();

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_books) {
                selectedFragment = new BooksFragment();
            } else if (id == R.id.nav_podcourse) {
                selectedFragment = new PodCourseFragment();
            } else if (id == R.id.nav_explore) {
                selectedFragment = new ExploreFragment();
            } else if (id == R.id.nav_challenge) {
                selectedFragment = new ChallengesFragment();
            } else if (id == R.id.nav_library) {
                selectedFragment = new LibraryFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });
    }
}