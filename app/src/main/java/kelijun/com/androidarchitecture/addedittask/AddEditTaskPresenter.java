package kelijun.com.androidarchitecture.addedittask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import kelijun.com.androidarchitecture.data.Task;
import kelijun.com.androidarchitecture.data.source.TasksDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ${kelijun} on 2018/6/22.
 */

public class AddEditTaskPresenter implements AddEditTaskContract.Persenter ,TasksDataSource.GetTaskCallback{

    @NonNull
    private TasksDataSource mTasksRepository;
    @NonNull
    private AddEditTaskContract.View mAddTaskView;
    private String mTaskId;

    private boolean mIsDataMissing;

    public AddEditTaskPresenter(@Nullable String taskId, @NonNull TasksDataSource tasksRepository,
                                @NonNull AddEditTaskContract.View addTaskView, boolean shouldLoadDataFromRepo) {
        this.mTaskId = taskId;
        this.mTasksRepository = checkNotNull(tasksRepository);
        this.mAddTaskView = checkNotNull(addTaskView);
        this.mIsDataMissing = shouldLoadDataFromRepo;

        mAddTaskView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewTask() && mIsDataMissing) {
            populateTask();
        }
    }
    private boolean isNewTask() {
        return mTaskId == null;
    }
    @Override
    public void saveTask(String title, String description) {
        if (isNewTask()) {
            createTask(title, description);
        }else{
            updateTask(title, description);
        }
    }
    private void createTask(String title, String description) {
        Task newTask = new Task(title, description);
        if (newTask.isEmpty()) {
            mAddTaskView.showEmptyTaskError();
        } else {
            mTasksRepository.saveTask(newTask);
            mAddTaskView.showTasksList();
        }
    }
    private void updateTask(String title, String description) {
        if (isNewTask()) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        mTasksRepository.saveTask(new Task(title, description, mTaskId));
        mAddTaskView.showTasksList(); // After an edit, go back to the list.
    }
    @Override
    public void populateTask() {
        if (isNewTask()) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }
        mTasksRepository.getTask(mTaskId, this);
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    @Override
    public void onTaskLoaded(Task task) {
        if (mAddTaskView.isActive()) {
            mAddTaskView.setTitle(task.getTitle());
            mAddTaskView.setDescription(task.getDescription());
        }
        mIsDataMissing = false;
    }

    @Override
    public void onDataNotAvailable() {
        if (mAddTaskView.isActive()) {
            mAddTaskView.showEmptyTaskError();
        }
    }
}
