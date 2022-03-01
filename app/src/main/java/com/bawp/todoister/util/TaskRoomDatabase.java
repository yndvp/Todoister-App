package com.bawp.todoister.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.bawp.todoister.data.TaskDao;
import com.bawp.todoister.model.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class TaskRoomDatabase extends RoomDatabase {
    public static final int NUMBER_OF_THREADS = 4;
    public static final String DATABASE_NAME = "todoister_database";
    private static volatile TaskRoomDatabase INSTANCE;
    public static final ExecutorService databaseWriterExecutor
            = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static final RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    /*
                        This allow us to do things in the background
                        which is what we always want to do when it comes to
                        databases and writing things into database
                     */
                    databaseWriterExecutor.execute(() -> {
                        // Invoke Dao, and write
                        TaskDao taskDao = INSTANCE.taskDao();

                        taskDao.deleteAll(); // clean state!
                    });
                }
            };

    public static TaskRoomDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            /*
                This is all aboutt making sure that everything goes back
                to the background thread and there's no issues
             */
            synchronized (TaskRoomDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TaskRoomDatabase.class, DATABASE_NAME)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract TaskDao taskDao();
}
