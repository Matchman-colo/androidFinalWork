package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.widget.Toast;

import com.example.notepad.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoActivity extends AppCompatActivity {
    VideoView vv;
    static final int REQUEST_VIDEO_CAPTURE = 3;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_PERMISSIONS = 101;
    private Uri videoUri; // 保存视频URI
    private static final String TAG = "VideoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        vv = findViewById(R.id.vv);
        // 检查并请求权限
        checkPermissions();
    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                Toast.makeText(this, "需要所有权限才能录像", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private File createVideoFile() throws IOException {
        // 创建唯一的文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "VIDEO_" + timeStamp + ".mp4";

        // 获取视频存储目录
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (storageDir == null) {
            storageDir = getFilesDir(); // 回退到内部存储
        }

        // 确保目录存在
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            throw new IOException("无法创建目录: " + storageDir.getAbsolutePath());
        }

        return new File(storageDir, videoFileName);
    }

    public void takeVideo(View view) {
        // 创建视频文件路径
        File videoFile = null;
        try {
            videoFile = createVideoFile();
        } catch (IOException e) {
            Log.e(TAG, "创建视频文件失败: " + e.getMessage());
            Toast.makeText(this, "无法创建视频文件", Toast.LENGTH_SHORT).show();
            return;
        }

        // 使用 FileProvider 获取 URI
        videoUri = FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider",
                videoFile);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        // 添加输出URI
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        // 设置视频质量
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // 1 = 高质量

        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
            } catch (Exception e) {
                Log.e(TAG, "启动录像失败: " + e.getMessage(), e);
                Toast.makeText(this, "启动录像失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e(TAG, "没有应用可以处理录像意图");
            Toast.makeText(this, "没有找到录像应用", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // 关键：检查是否成功录制
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            // 使用我们保存的URI而不是intent返回的URI（更可靠）
            if (videoUri != null) {
                playVideo(videoUri);
            } else if (intent != null && intent.getData() != null) {
                playVideo(intent.getData());
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "录像已取消", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "录像失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void playVideo(Uri uri) {
        try {
            MediaController controller = new MediaController(this);
            vv.setVideoURI(uri);
            controller.setMediaPlayer(vv);
            vv.setMediaController(controller);
            vv.requestFocus();
            vv.start(); // 关键：必须调用start()方法
        } catch (Exception e) {
            Log.e(TAG, "播放视频错误: " + e.getMessage());
            Toast.makeText(this, "无法播放视频: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void backVideo(View view) {
        // 将视频路径返回给 EditActivity
        Intent intent = new Intent();
        intent.putExtra("video", videoUri.toString());
        setResult(RESULT_OK, intent);
        VideoActivity.this.finish();
    }
}
