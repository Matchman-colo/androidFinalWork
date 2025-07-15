package com.example.notepad;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.w3c.dom.Text;
@Entity
public class Data{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name="content")
    private String content;
    @ColumnInfo(name="data")
    private String data;

    @ColumnInfo(name="title")
    private String title;
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Data(String title, String content, String data) {
        this.title = title;
        this.content = content;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", title=" + title +
                ", content=" + content +
                ", data=" + data +
                '}';
    }
}
