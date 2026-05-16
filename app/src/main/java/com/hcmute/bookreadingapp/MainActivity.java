package com.hcmute.bookreadingapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int BESTSELLER_PAGE_INDEX = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupFeaturedCarousel();
    }

    private void setupFeaturedCarousel() {
        List<FeaturedBook> books = Arrays.asList(
                new FeaturedBook(
                        R.drawable.bia_duoc_hoc,
                        R.string.title_duoc_hoc,
                        R.string.book_duoc_hoc,
                        false),
                new FeaturedBook(
                        R.drawable.sapiens_luoc_su_loai_nguoi,
                        R.string.title_sapiens,
                        R.string.bestseller_cover,
                        true),
                new FeaturedBook(
                        R.drawable.chuyen_nho_sai_gon_bao_no,
                        R.string.title_chuyen_nho,
                        R.string.book_chuyen_nho,
                        false),
                new FeaturedBook(
                        R.drawable.kiep_nao_ta_cung_tim_thay_nhau,
                        R.string.title_kiep_nao,
                        R.string.book_kiep_nao,
                        false)
        );

        ViewPager2 pager = findViewById(R.id.featured_pager);
        pager.setAdapter(new FeaturedBookAdapter(books));
        pager.setOffscreenPageLimit(1);

        int sidePeek = Math.round(20 * getResources().getDisplayMetrics().density);
        RecyclerView recyclerView = (RecyclerView) pager.getChildAt(0);
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        recyclerView.setPadding(sidePeek, 0, sidePeek, 0);
        recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        pager.post(() -> pager.setCurrentItem(BESTSELLER_PAGE_INDEX, false));
    }
}
