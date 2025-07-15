package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lv;
    private NoteDatabase noteDatabase;
    private DataDao dataDao;
    private List<Data>mDatas=new ArrayList<>();
    private MainAdapter mainAdapter;
    private EditText searchEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=findViewById(R.id.main_ls);
        searchEdit = findViewById(R.id.search_edit); // 输入框
        noteDatabase=NoteDatabase.getDatabase(this);
        dataDao=noteDatabase.getDataDao();
        mDatas=getData();
        mainAdapter=new MainAdapter(this,mDatas);
        lv.setAdapter(mainAdapter);
        lv.setOnItemLongClickListener(new ItemLongClickEvent());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Data selectedItem = mDatas.get(position);
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("editModel", "update");
                intent.putExtra("noteId", selectedItem.getId());
                startActivity(intent);
            }
        });

        // 添加放大镜图标的点击事件
        findViewById(R.id.main_top_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchEdit.getText().toString();
                if (!searchText.isEmpty()) {
                    searchNotes(searchText);
                } else {
                    Toast.makeText(MainActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 添加 TextWatcher 到输入框
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入框内容变化时，如果内容为空，则重新加载所有笔记
                if (s.toString().isEmpty()) {
                    mDatas = getData();
                    mainAdapter = new MainAdapter(MainActivity.this, mDatas);
                    lv.setAdapter(mainAdapter);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    int ii;
    //记事列表长按监听器
    class ItemLongClickEvent implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            ii=0;
            final String []s={"删除记事本"};
            AlertDialog.Builder DanItem = new AlertDialog.Builder(MainActivity.this);
            DanItem.setTitle("操作");
            DanItem.setSingleChoiceItems(s, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ii =which;
                }
            });
            DanItem.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which){
                    Toast.makeText(MainActivity.this, "已"+s[ii], Toast.LENGTH_LONG).show();
                    Data tmpd=mDatas.get(position);
                    if (ii==0){
                        dataDao.deleteById(tmpd.getId());
                        mDatas=getData();
                        mainAdapter=new MainAdapter(MainActivity.this,mDatas);
                        lv.setAdapter(mainAdapter);
                    }
                }
            });
            DanItem.create().show();
            return true;
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        noteDatabase=NoteDatabase.getDatabase(this);
        dataDao=noteDatabase.getDataDao();
        mDatas=getData();
        MainAdapter mainAdapter=new MainAdapter(this,mDatas);
        lv.setAdapter(mainAdapter);
    }

    public List<Data>getData(){
        return dataDao.findAll();
    }

    public void main_add(View view) {
        Intent intent=new Intent(this,EditActivity.class);
        startActivity(intent);
    }

    // 用于实现搜索功能
    private void searchNotes(String searchText) {
        List<Data> searchResults = dataDao.findByTitleContaining(searchText);
        mainAdapter = new MainAdapter(this, searchResults);
        lv.setAdapter(mainAdapter);
    }
}