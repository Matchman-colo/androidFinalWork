package com.example.notepad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class ShowVideo extends AppCompatActivity {
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);

        videoView = findViewById(R.id.videoView);
        String videoPath = getIntent().getStringExtra("videoPath");

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 关闭当前活动，返回上一个活动
            }
        });

        if (videoPath != null) {
            Uri uri = Uri.parse(videoPath);
            videoView.setVideoURI(uri);
            videoView.start();
        }
    }
}