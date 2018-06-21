package kelijun.com.androidarchitecture.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kelijun.com.androidarchitecture.data.Task;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ${kelijun} on 2018/6/21.
 */

public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCE = null;

    private static TasksDataSource mTasksRemoteDataSource;

    private static TasksDataSource mTasksLocalDataSource;

    Map<String, Task> cacheTasks;

    private boolean mCacheIsDirty = false;

    private TasksRepository(@NonNull TasksDataSource taskRemoteDataSource,
                            @NonNull TasksDataSource taskLocalDataSource) {
        mTasksLocalDataSource = taskLocalDataSource;
        mTasksRemoteDataSource = taskRemoteDataSource;
    }

    public static TasksRepository getInstance(TasksDataSource taskRemoteDataSource,
                                              TasksDataSource taskLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(taskRemoteDataSource, taskLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destoryInstance() {
        INSTANCE = null;
    }

    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        checkNotNull(callback);

        //缓存有并且可用(不是dirty的)使用缓存的数据
        if (cacheTasks != null && !mCacheIsDirty) {
            //获取map中的所有值 .value(),然后存放到arraylist中
            callback.onTasksLoaded(new ArrayList<Task>(cacheTasks.values()));
            return;
        }
        //如果缓存的数据不可用
        if (mCacheIsDirty) {
            //从remote (network)获取数据
            getTaskFromRemoteDataSource(callback);
        } else {
            //没有缓存,从本地获取
            mTasksLocalDataSource.getTasks(new LoadTasksCallback() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    refreshCache(tasks);
                    callback.onTasksLoaded(new ArrayList<Task>(cacheTasks.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    //本地获取不到数据,从远程获取
                    getTaskFromRemoteDataSource(callback);
                }
            });

        }
    }

    private void getTaskFromRemoteDataSource(@NonNull final LoadTasksCallback callback) {
        mTasksRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                //更新缓存
                refreshCache(tasks);
                //更新本地数据
                refreshLocalSource(tasks);
                //通知tasksLoaded
                callback.onTasksLoaded(new ArrayList<Task>(cacheTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                //通知taskData NotAvailable
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshLocalSource(List<Task> tasks) {
        mTasksLocalDataSource.deleteAllTasks();
        for (Task task : tasks
                ) {
            mTasksLocalDataSource.saveTask(task);
        }
    }

    private void refreshCache(List<Task> tasks) {
        if (cacheTasks == null) {
            cacheTasks = new LinkedHashMap<>();
        }
        cacheTasks.clear();
        for (Task task : tasks
                ) {
            cacheTasks.put(task.getId(), task);
        }
        mCacheIsDirty = false;
    }

    @Override
    public void getTask(@NonNull final String taskId, @NonNull final GetTaskCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);

        final Task cachedTask = getTaskWithId(taskId);
        if (cachedTask != null) {
            callback.onTaskLoaded(cachedTask);
            return;
        }

        mTasksLocalDataSource.getTask(taskId, new GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                if (cacheTasks == null) {
                    cacheTasks = new LinkedHashMap<>();
                }
                cacheTasks.put(taskId, task);
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                mTasksRemoteDataSource.getTask(taskId, new GetTaskCallback() {
                    @Override
                    public void onTaskLoaded(Task task) {
                        //本地数据更新
                        if (cacheTasks == null) {
                            cacheTasks = new LinkedHashMap<>();
                        }
                        cacheTasks.put(taskId, task);
                        callback.onTaskLoaded(task);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });

    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksLocalDataSource.saveTask(task);
        mTasksRemoteDataSource.saveTask(task);
        //缓存task
        if (cacheTasks == null) {
            cacheTasks = new LinkedHashMap<>();
        }
        cacheTasks.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksLocalDataSource.completeTask(task);
        mTasksRemoteDataSource.completeTask(task);

        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);

        //保存在缓存中
        if (cacheTasks == null) {
            cacheTasks = new LinkedHashMap<>();
        }
        cacheTasks.put(completedTask.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        completeTask(getTaskWithId(taskId));
    }

    @Nullable
    private Task getTaskWithId(@NonNull String taskId) {
        checkNotNull(taskId);
        if (cacheTasks == null || cacheTasks.isEmpty()) {
            return null;
        } else {
            return cacheTasks.get(taskId);
        }
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.activateTask(task);
        mTasksLocalDataSource.activateTask(task);

        Task activateTask = new Task(task.getTitle(), task.getDescription(), task.getId());

        if (cacheTasks == null) {
            cacheTasks = new LinkedHashMap<>();
        }

        cacheTasks.put(activateTask.getId(), activateTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        checkNotNull(taskId);
        activateTask(getTaskWithId(taskId));
    }

    @Override
    public void clearCompleteTasks() {
        mTasksLocalDataSource.clearCompleteTasks();
        mTasksRemoteDataSource.clearCompleteTasks();

        if (cacheTasks == null) {
            cacheTasks = new LinkedHashMap<>();
        }
        //map元素的遍历
        Iterator<Map.Entry<String, Task>> it = cacheTasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        mTasksRemoteDataSource.deleteAllTasks();
        mTasksLocalDataSource.deleteAllTasks();

        if (cacheTasks == null) {
            cacheTasks = new LinkedHashMap<>();
        }
        cacheTasks.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        mTasksLocalDataSource.deleteTask(checkNotNull(taskId));
        mTasksRemoteDataSource.deleteTask(checkNotNull(taskId));

        cacheTasks.remove(taskId);
    }
}
