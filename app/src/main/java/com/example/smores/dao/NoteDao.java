package com.example.smores.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.smores.entities.Smores;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM smores ORDER BY id DESC")
    List<Smores> getAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Smores smore);

    @Delete
    void deleteNote(Smores smore);
}
