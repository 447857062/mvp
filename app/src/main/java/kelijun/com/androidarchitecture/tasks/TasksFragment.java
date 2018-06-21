package kelijun.com.androidarchitecture.tasks;

import android.support.v4.app.Fragment;

import java.util.List;

import kelijun.com.androidarchitecture.data.Task;

/**
 * Created by ${kelijun} on 2018/6/21.
 */

public class TasksFragment extends Fragment implements TasksContract.View{
    @Override
    public void setPresenter(TasksContract.Persenter presenter) {

    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showTasks(List<Task> tasks) {

    }

    @Override
    public void showAddTask() {

    }

    @Override
    public void showTaskDetailsUi(String taskId) {

    }

    @Override
    public void showTaskMarkedComplete() {

    }

    @Override
    public void showTaskMarkedActive() {

    }

    @Override
    public void showCompltedTaskCleared() {

    }

    @Override
    public void showLoadingTasksError() {

    }

    @Override
    public void showNoTasks() {

    }

    @Override
    public void showActiveFilterLable() {

    }

    @Override
    public void showCompleteFilterLable() {

    }

    @Override
    public void showAllFilterLable() {

    }

    @Override
    public void showNoActiveTasks() {

    }

    @Override
    public void showNoCompletedTasks() {

    }

    @Override
    public void showSuccessfullySavedMessage() {

    }

    @Override
    public void isActive() {

    }

    @Override
    public void showFilterPopUpMenu() {

    }
}
