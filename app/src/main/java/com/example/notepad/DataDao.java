package com.example.notepad;

import androidx.constraintlayout.solver.Cache;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DataDao {
    @Insert
    void insertData(Data data);
    @Query("delete from data")
    void deleteAll();
    @Query("select * from data")
    List<Data> findAll();
    @Query("delete from data where id =:pos")
    void deleteById(int pos);
    @Query("select * from data where id =:pos")
    Data findById(int pos);
    @Update
    int update_all(Data cache);
    @Query("SELECT * FROM data WHERE title LIKE '%' || :title || '%'")
    List<Data> findByTitleContaining(String title);
}
