package com.hcmute.bookreadingapp.ui.audio;
import com.hcmute.bookreadingapp.ui.audio.AudioPlayerActivity;

import com.hcmute.bookreadingapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class PodCourseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcourse_detail);

        LinearLayout lesson1 = findViewById(R.id.layout_lesson_1);
        if (lesson1 != null) {
            lesson1.setOnClickListener(v -> {
                Intent intent = new Intent(PodCourseDetailActivity.this, AudioPlayerActivity.class);
                startActivity(intent);
            });
        }
    }
}