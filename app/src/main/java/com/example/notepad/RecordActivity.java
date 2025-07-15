package com.example.notepad;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class RecordActivity extends AppCompatActivity {

    private ImageView btn_record;
    private ImageView iv_microphone;
    private TextView tv_recordTime;
    private ImageView iv_record_wave_left,iv_record_wave_right;

    private AnimationDrawable ad_left,ad_right;

    private int isRecording = 0;
    private int isPlaying = 0;

    private Timer mTimer;
    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    //语音保存路径
    private String FilePath = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // 检查录音权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            //setupRecorder();
        }

        Button btn_save = (Button)findViewById(R.id.bt_save);
        btn_save.setOnClickListener((View.OnClickListener) new ClickEvent());
        Button btn_back = (Button)findViewById(R.id.bt_back);
        btn_back.setOnClickListener((View.OnClickListener) new ClickEvent());



        btn_record = findViewById(R.id.btn_record);
        btn_record.setOnClickListener((View.OnClickListener) new ClickEvent());

        iv_microphone = (ImageView)findViewById(R.id.iv_microphone);
        iv_microphone.setOnClickListener((View.OnClickListener) new ClickEvent());

        iv_record_wave_left = (ImageView)findViewById(R.id.iv_record_wave_left);
        iv_record_wave_right = (ImageView)findViewById(R.id.iv_record_wave_right);

        ad_left = (AnimationDrawable)iv_record_wave_left.getBackground();
        ad_right = (AnimationDrawable)iv_record_wave_right.getBackground();
        tv_recordTime = (TextView)findViewById(R.id.tv_recordTime);
    }

    final Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1 :
                    String time[] = tv_recordTime.getText().toString().split(":");
                    int hour = Integer.parseInt(time[0]);
                    int minute = Integer.parseInt(time[1]);
                    int second = Integer.parseInt(time[2]);
                    if(second < 59){
                        second++;
                    }
                    else if(second == 59 && minute < 59){
                        minute++;second = 0;
                    }
                    if(second == 59 && minute == 59 && hour < 98){
                        hour++;minute = 0;second = 0;
                    }
                    time[0] = hour + "";
                    time[1] = minute + "";
                    time[2] = second + "";
                    if(second < 10) time[2] = "0" + second;
                    if(minute < 10) time[1] = "0" + minute;
                    if(hour < 10) time[0] = "0" + hour;
                    tv_recordTime.setText(time[0]+":"+time[1]+":"+time[2]);
                    break;
            }

        }
    };

    class ClickEvent implements View.OnClickListener {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
                if(v.getId() == R.id.btn_record) {             //点击的是开始录音按钮
                    if (isRecording == 0) {
                        if (FilePath != null) {
                            File oldFile = new File(FilePath);
                            oldFile.delete();
                        }
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                        String str = formatter.format(curDate);

                        str = str + "record.amr";
                        File dir = new File(getExternalFilesDir(null), "notes");
                        if (!dir.exists()) {
                            dir.mkdirs(); // 创建目录
                        }
                        File file = new File(dir, str + "record.amr");
                        FilePath = file.getAbsolutePath();
                        if (file.exists()) {
                            file.delete();
                        }

                        mTimer = new Timer();
                        iv_microphone.setClickable(false);
                        tv_recordTime.setText("00:00:00");
                        isRecording = 1;
                        btn_record.setBackgroundResource(R.drawable.iv_record_stop);

                        mRecorder = new MediaRecorder();
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mRecorder.setOutputFile(FilePath);
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                        try {
                            mRecorder.prepare();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mRecorder.start();
                        mTimer.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);

                            }
                        }, 1000, 1000);
                        ad_left.start();
                        ad_right.start();
                    } else {
                        isRecording = 0;
                        btn_record.setBackgroundResource(R.drawable.iv_record_begin);
                        mRecorder.stop();
                        mTimer.cancel();
                        mTimer = null;

                        mRecorder.release();
                        mRecorder = null;
                        iv_microphone.setClickable(true);
                        //停止动画
                        ad_left.stop();
                        ad_right.stop();
                        Toast.makeText(RecordActivity.this, "单击麦克图标试听，再次点击结束试听", Toast.LENGTH_LONG).show();
                    }
                }else if(v.getId() == R.id.iv_microphone) {
                    if (FilePath == null)
                        Toast.makeText(RecordActivity.this, "请先录音", Toast.LENGTH_LONG).show();
                    else {
                        if (isPlaying == 0) {
                            isPlaying = 1;
                            tv_recordTime.setText("00:00:00");
                            mPlayer = new MediaPlayer();
                            mTimer = new Timer();
                            mPlayer.setOnCompletionListener((MediaPlayer.OnCompletionListener) new MediaCompletion());
                            try {
                                mPlayer.setDataSource(FilePath);
                                mPlayer.prepare();
                                mPlayer.start();
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mTimer.schedule(new TimerTask() {

                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);

                                }
                            }, 1000, 1000);
                            ad_left.start();
                            ad_right.start();
                        } else {
                            isPlaying = 0;
                            mPlayer.stop();
                            mPlayer.release();
                            mPlayer = null;
                            mTimer.cancel();
                            mTimer = null;
                            //停止动画
                            ad_left.stop();
                            ad_right.stop();
                        }
                    }
                }else if(v.getId() == R.id.bt_save) {
                    Intent intent = getIntent();
                    Bundle b = new Bundle();
                    b.putString("audio", FilePath);
                    System.out.println(FilePath);
                    intent.putExtras(b);
                    setResult(RESULT_OK, intent);
                    RecordActivity.this.finish();
                }else if(v.getId() == R.id.bt_back) {
                    if (FilePath != null) {
                        File oldFile = new File(FilePath);
                        oldFile.delete();
                    }
                    RecordActivity.this.finish();
                }
        }
    }

    class MediaCompletion implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            mTimer.cancel();
            mTimer = null;
            isPlaying = 0;
            //停止动画
            ad_left.stop();
            ad_right.stop();
            Toast.makeText(RecordActivity.this, "播放完毕", Toast.LENGTH_LONG).show();
            tv_recordTime.setText("00:00:00");
        }

    }
}
