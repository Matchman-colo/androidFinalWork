package com.example.notepad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ShowPicture extends AppCompatActivity {
    private ImageView fullScreenImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);

        fullScreenImageView = findViewById(R.id.fullScreenImageView);
        Button backButton = findViewById(R.id.backButton);

        // 获取从 EditActivity 传递过来的图片路径
        String imagePath = getIntent().getStringExtra("imgPath");

        // 将图片显示在 ImageView 中
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            fullScreenImageView.setImageBitmap(bitmap);
        }

        // 为返回按钮设置点击事件
        backButton.setOnClickListener(view -> finish());
    }
}