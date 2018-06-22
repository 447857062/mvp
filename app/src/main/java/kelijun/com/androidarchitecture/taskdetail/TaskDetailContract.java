package kelijun.com.androidarchitecture.taskdetail;

import kelijun.com.androidarchitecture.BasePersenter;
import kelijun.com.androidarchitecture.BaseView;

/**
 * Created by ${kelijun} on 2018/6/22.
 */

public interface TaskDetailContract {
    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean isActive);

        void showMissingTask();

        void hideTitle();

        void showTitle(String title);

        void showDescription(String description);

        void hideDescription();

        void showCompletionStatus(boolean complete);

        void showEditTask(String taskId);

        void showTaskDeleted();

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        boolean isActive();

    }
    interface Presenter extends BasePersenter {
        void editTask();

        void deleteTask();

        void completeTask();

        void activateTask();
    }
}
