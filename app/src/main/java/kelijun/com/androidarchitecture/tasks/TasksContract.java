package kelijun.com.androidarchitecture.tasks;

import android.support.annotation.NonNull;

import java.util.List;

import kelijun.com.androidarchitecture.BasePersenter;
import kelijun.com.androidarchitecture.BaseView;
import kelijun.com.androidarchitecture.data.Task;

/**
 * Created by ${kelijun} on 2018/6/21.
 */

public interface TasksContract {
    interface View extends BaseView<Persenter> {
        void setLoadingIndicator(boolean active);

        void showTasks(List<Task> tasks);

        void showAddTask();

        void showTaskDetailsUi(String taskId);

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        void showCompltedTaskCleared();

        void showLoadingTasksError();

        void showNoTasks();

        void showActiveFilterLable();

        void showCompleteFilterLable();

        void showAllFilterLable();

        void showNoActiveTasks();

        void showNoCompletedTasks();

        void showSuccessfullySavedMessage();

        boolean isActive();

        void showFilterPopUpMenu();

    }

    interface Persenter extends BasePersenter {
        void result(int requestCode, int resultCode);

        void loadTasks(boolean forceUpdate);

        void addNewTask();

        void openTaskDetails(@NonNull Task requestedTask);

        void completeTask(@NonNull Task completedTask);

        void activeTask(@NonNull Task activeTask);

        void clearCompletedTasks();

        void setFiltering(TasksFilterType requestType);

        TasksFilterType getFiltering();
    }
}
