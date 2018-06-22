package kelijun.com.androidarchitecture.statistics;

import android.support.annotation.NonNull;

import java.util.List;

import kelijun.com.androidarchitecture.data.Task;
import kelijun.com.androidarchitecture.data.source.TasksDataSource;
import kelijun.com.androidarchitecture.data.source.TasksRepository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ${kelijun} on 2018/6/22.
 */

public class StatisticsPresenter implements StatisticsContract.Presenter {

    private final TasksRepository mTasksRepository;

    private final StatisticsContract.View mStatisticsView;

    public StatisticsPresenter(@NonNull TasksRepository tasksRepository,
                               @NonNull StatisticsContract.View statisticsView) {
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null");
        mStatisticsView = checkNotNull(statisticsView, "StatisticsView cannot be null!");

        mStatisticsView.setPresenter(this);
    }
    @Override
    public void start() {
        loadStatistics();
    }

    private void loadStatistics() {
        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                int activeTask=0;
                int completedTask = 0;

                for (Task task :tasks
                        ) {
                    if (task.isCompleted()) {
                        completedTask+=1;
                    }else{
                        activeTask += 1;
                    }
                }
                if (!mStatisticsView.isActive()) {
                    return;
                }
                mStatisticsView.setProgressIndictor(false);
                mStatisticsView.showStatistics(activeTask,completedTask);
            }

            @Override
            public void onDataNotAvailable() {
                if (!mStatisticsView.isActive()) {
                    return;
                }
                mStatisticsView.showLoadingStatisticsError();
            }
        });
    }
}
