package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ShowRecord extends Activity {

    private String audioPath;
    private int isPlaying = 0;
    private AnimationDrawable ad_left,ad_right;
    private Timer mTimer;
    //语音操作对象
    private MediaPlayer mPlayer = null;
    private ImageView iv_record_wave_left,iv_record_wave_right,iv_microphone;
    private TextView tv_recordTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record);
        Intent intent = this.getIntent();
        audioPath = intent.getStringExtra("audioPath");
        System.out.println(audioPath);
        iv_microphone = (ImageView)findViewById(R.id.iv_microphone);
        iv_microphone.setOnClickListener((View.OnClickListener) new ClickEvent());

        iv_record_wave_left = (ImageView)findViewById(R.id.iv_record_wave_left);
        iv_record_wave_right = (ImageView)findViewById(R.id.iv_record_wave_right);

        ad_left = (AnimationDrawable)iv_record_wave_left.getBackground();
        //ad_left = (AnimationDrawable)iv_record_wave_left.getDrawable();
        ad_right = (AnimationDrawable)iv_record_wave_right.getBackground();
        //ad_right = (AnimationDrawable)iv_record_wave_right.getDrawable();
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
                        minute++;
                        second = 0;

                    }
                    if(second == 59 && minute == 59 && hour < 98){
                        hour++;
                        minute = 0;
                        second = 0;
                    }

                    time[0] = hour + "";
                    time[1] = minute + "";
                    time[2] = second + "";
                    //调整格式显示到屏幕上
                    if(second < 10)
                        time[2] = "0" + second;
                    if(minute < 10)
                        time[1] = "0" + minute;
                    if(hour < 10)
                        time[0] = "0" + hour;

                    //显示在TextView中
                    tv_recordTime.setText(time[0]+":"+time[1]+":"+time[2]);

                    break;

            }

        }
    };

    public void back_show_rec(View view) {
        ShowRecord.this.finish();
    }

    class ClickEvent implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            //试听
            if(isPlaying == 0){
                isPlaying = 1;
                mPlayer = new MediaPlayer();
                tv_recordTime.setText("00:00:00");
                mTimer = new Timer();
                mPlayer.setOnCompletionListener((MediaPlayer.OnCompletionListener) new MediaCompletion());
                try {
                    mPlayer.setDataSource(audioPath);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }, 1000,1000);
                //播放动画
                ad_left.start();
                ad_right.start();
            }
            //结束试听
            else{
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
            Toast.makeText(ShowRecord.this, "播放完毕", Toast.LENGTH_SHORT).show();
            tv_recordTime.setText("00:00:00");
        }
    }
}