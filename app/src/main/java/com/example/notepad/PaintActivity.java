package com.example.notepad;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.notepad.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class PaintActivity extends AppCompatActivity {
    private ImageView iv;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private float startX=0f;
    private float startY=0f;
    private Button bt_back,bt_sure;
    private ImageView res;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        iv=findViewById(R.id.iv);
        res=findViewById(R.id.res);
        bt_back=findViewById(R.id.bt_back);
        bt_sure=findViewById(R.id.paint_sure);
        bitmap=Bitmap.createBitmap(1000,1000,Bitmap.Config.ARGB_8888);
        paint=new Paint();
        canvas=new Canvas(bitmap);
        iv.setImageBitmap(bitmap);
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float endX = event.getX();
                        float endY = event.getY();
                        canvas.drawLine(startX,startY,endX,endY,paint);
                        iv.setImageBitmap(bitmap);
                        startX = endX;
                        startY = endY;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
    }
    int id;
    public void setColor(View view) {
        id=0;
        final int [] col={Color.RED,Color.YELLOW,Color.GREEN,Color.BLUE,Color.WHITE,Color.BLACK};
        final String []s={"红色","黄色","绿色","蓝色","白色","黑色"};
        AlertDialog.Builder DanItem = new AlertDialog.Builder(PaintActivity.this);
        DanItem.setTitle("选择想要使用的颜色");
        DanItem.setSingleChoiceItems(s, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                id =which;
            }
        });
        DanItem.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                ShowMessage("恭喜你选择了"+s[id].toString());
                paint.setColor(col[id]);
            }
        });
        DanItem.create().show();
    }
    private void ShowMessage(String str) {
        Toast.makeText(PaintActivity.this, str, Toast.LENGTH_LONG).show();
    }
    public void setWidth(View view) {
        id=0;
        final int [] w={5,10,15,20,25,30,35,40};
        final String []s={"5","10","15","20","25","30"};
        AlertDialog.Builder DanItem = new AlertDialog.Builder(PaintActivity.this);
        DanItem.setTitle("选择想要使用的宽度");
        DanItem.setSingleChoiceItems(s, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                id =which;
            }
        });
        DanItem.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                ShowMessage("恭喜你选择了"+s[id]);
                paint.setStrokeWidth(w[id]);
            }
        });
        DanItem.create().show();
    }

    public void setErase(View view) {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(30);
    }
    public void paint_back(View view){
        PaintActivity.this.finish();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void savePng(View view) throws FileNotFoundException {
        Intent intent = getIntent();
        Bundle b = new Bundle();
        String path = saveBitmap(bitmap);
        b.putString("paintPath", path);
        intent.putExtras(b);
        setResult(RESULT_OK, intent);
        PaintActivity.this.finish();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String saveBitmap(Bitmap mBitmap){
        //获得系统当前时间，并以该时间作为文件名
        SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");
        Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间
        String   str   =   formatter.format(curDate);
        String paintPath = "";
        str = str + "paint.png";
        File dir = new File("/sdcard/notes/");
        File file = new File("/sdcard/notes/",str);
        if (!dir.exists()) {
            dir.mkdir();
        }
        else{
            if(file.exists()){
                file.delete();
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            //保存绘图文件路径
            paintPath = "/sdcard/notes/" + str;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return paintPath;
    }
}
