package kelijun.com.androidarchitecture.data.source;

import android.support.annotation.NonNull;

import java.util.List;

import kelijun.com.androidarchitecture.data.Task;

/**
 * Created by ${kelijun} on 2018/6/21.
 */

public interface TasksDataSource {
    interface LoadTasksCallback{
        void onTasksLoaded(List<Task> tasks);

        void onDataNotAvailable();
    }
    interface GetTaskCallback{
        void onTaskLoaded(Task task);
        void onDataNotAvailable();
    }
    void getTasks(@NonNull LoadTasksCallback callback);
    void getTask(@NonNull String taskId,@NonNull GetTaskCallback callback);
    void saveTask(@NonNull Task task);
    void completeTask(@NonNull Task task);
    void completeTask(@NonNull String taskId);
    void activateTask(@NonNull Task task);
    void activateTask(@NonNull String taskId);
    void clearCompleteTasks();
    void refreshTasks();
    void deleteAllTasks();
    void deleteTask(@NonNull String taskId);
}
