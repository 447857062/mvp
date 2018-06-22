package kelijun.com.androidarchitecture.statistics;

import kelijun.com.androidarchitecture.BasePersenter;
import kelijun.com.androidarchitecture.BaseView;

/**
 * Created by ${kelijun} on 2018/6/22.
 */

public interface StatisticsContract {
    interface View extends BaseView<Presenter> {
        void setProgressIndictor(boolean active);

        void showStatistics(int numberOfIncompleteTasks, int numberOfCompletedTasks);

        void showLoadingStatisticsError();

        boolean isActive();
    }

    interface Presenter extends BasePersenter {
    }

}
