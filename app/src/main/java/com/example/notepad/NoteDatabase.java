package com.example.notepad;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {Data.class},version = 1,exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase INSTANCE;
    static public NoteDatabase getDatabase(Context context){
        if(INSTANCE==null) {
            INSTANCE= Room.databaseBuilder(context,NoteDatabase.class,"note_database")
                    .allowMainThreadQueries().build();
            //数据库名 note_database
            //表名 Data
        }
        return INSTANCE;
    }
    public abstract DataDao getDataDao();
}
