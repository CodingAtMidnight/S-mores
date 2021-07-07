package com.example.smores.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.smores.dao.NoteDao;
import com.example.smores.entities.Smores;

@Database(entities = Smores.class, version = 1, exportSchema = false)
public abstract class SmoresDatabase extends RoomDatabase {

    private static SmoresDatabase smoresDatabase;

    public static synchronized SmoresDatabase getDatabase(Context context) {
        if (smoresDatabase == null) {
            smoresDatabase = Room.databaseBuilder(
                    context,
                    SmoresDatabase.class,
                    "smores_db"
            ).build();
        }
        return smoresDatabase;
    }
  public abstract NoteDao noteDao();
}
