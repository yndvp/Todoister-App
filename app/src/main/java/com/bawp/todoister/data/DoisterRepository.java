package com.bawp.todoister.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.bawp.todoister.model.Task;
import com.bawp.todoister.util.TaskRoomDatabase;

import java.util.List;

public class DoisterRepository {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;

    public DoisterRepository(Application application) {
        TaskRoomDatabase db = TaskRoomDatabase.getDatabase(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    public void insert(Task task) {
        TaskRoomDatabase.databaseWriterExecutor.execute(() -> {
            taskDao.insert(task);
        });
    }

    public void update(Task task) {
        // Run things in the background thread
        TaskRoomDatabase.databaseWriterExecutor.execute(() -> {
            taskDao.update(task);
        });
    }

    public void delete(Task task) {
        TaskRoomDatabase.databaseWriterExecutor.execute(() -> {
            taskDao.delete(task);
        });
    }

    public LiveData<Task> getTask(int id) { return taskDao.getTask(id); }

    public LiveData<List<Task>> getAllTasks () { return allTasks; }
}
