package kelijun.com.androidarchitecture.tasks;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import kelijun.com.androidarchitecture.data.Task;
import kelijun.com.androidarchitecture.data.source.TasksDataSource;
import kelijun.com.androidarchitecture.data.source.TasksRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ${kelijun} on 2018/6/21.
 */

public class TasksPersenter implements TasksContract.Persenter {
    private final TasksRepository mTasksRepository;
    private final TasksContract.View mTaskView;

    private boolean mFirstLoad = true;

    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    public TasksPersenter(@NonNull TasksRepository tasksRepository, @NonNull TasksContract.View taskView) {
        this.mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null");
        this.mTaskView = checkNotNull(taskView, "tasksView cannot be null!");

        mTaskView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTasks(false);
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    @Override
    public void addNewTask() {
        mTaskView.showAddTask();
    }

    @Override
    public void openTaskDetails(@NonNull Task requestedTask) {
        checkNotNull(requestedTask, "requestTask cannot be null");
        mTaskView.showTaskDetailsUi(requestedTask.getId());
    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        checkNotNull(completedTask, "completedTask cannot be null");
        mTasksRepository.completeTask(completedTask);
        mTaskView.showTaskMarkedComplete();
        loadTasks(false, false);

    }

    @Override
    public void activeTask(@NonNull Task activeTask) {
        checkNotNull(activeTask, "activeTask cannot be null");
        mTasksRepository.activateTask(activeTask);
        mTaskView.showTaskMarkedActive();
        loadTasks(false, false);
    }

    @Override
    public void clearCompletedTasks() {
        mTasksRepository.clearCompleteTasks();
        mTaskView.showCompltedTaskCleared();
        loadTasks(false,false);
    }

    @Override
    public void setFiltering(TasksFilterType requestType) {
        mCurrentFiltering=requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link TasksDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mTaskView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mTasksRepository.refreshTasks();
        }

        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                List<Task> tasksToShow = new ArrayList<>();
                for (Task task : tasks
                        ) {
                    switch (mCurrentFiltering) {
                        case ALL_TASKS:
                            tasksToShow.add(task);
                            break;
                        case COMPLETED_TASKS:
                            if(task.isCompleted()){
                                tasksToShow.add(task);
                            }

                            break;
                        case ACTIVE_TASKS:
                            if (task.isActive()) {
                                tasksToShow.add(task);
                            }
                            break;
                            default:
                                tasksToShow.add(task);
                                break;
                    }
                }
                if (!mTaskView.isActive()) {
                    return;
                }
                if (showLoadingUI) {
                    mTaskView.setLoadingIndicator(false);
                }
                processTasks(tasksToShow);
            }

            @Override
            public void onDataNotAvailable() {
                if (!mTaskView.isActive()) {
                    return;
                }
                mTaskView.showLoadingTasksError();
            }
        });
    }

    private void processTasks(List<Task> tasksToShow) {
        if (tasksToShow.isEmpty()) {
            processEmptyTasks();
        } else {
            mTaskView.showTasks(tasksToShow);
            showFilterLable();
        }
    }

    private void showFilterLable() {
        switch (mCurrentFiltering){

            case COMPLETED_TASKS:
                mTaskView.showCompleteFilterLable();
                break;
            case ACTIVE_TASKS:
                mTaskView.showAllFilterLable();
                break;
                default:
                    mTaskView.showAllFilterLable();
                    break;
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFiltering){
            case ACTIVE_TASKS:
                mTaskView.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                mTaskView.showNoCompletedTasks();
                break;
           default:
               mTaskView.showNoTasks();
                break;
        }
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a task was successfully added, show snackbar
        //TODO
    }
}
