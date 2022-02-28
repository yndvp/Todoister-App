package com.bawp.todoister.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bawp.todoister.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM task_table")
    void deleteAll();

    @Query("SELECT * FROM task_table WHERE task_table.task_id == :id")
    /*
        LiveData: When the data changes, anything that has been listening to that data
        (in this case: our user interface) will also change automatically
     */
    LiveData<Task> getTask(long id);

    @Query("SELECT * FROM task_table")
    LiveData<List<Task>> getAllTasks();
}
