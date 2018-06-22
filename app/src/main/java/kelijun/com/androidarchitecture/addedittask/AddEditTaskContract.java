package kelijun.com.androidarchitecture.addedittask;

import kelijun.com.androidarchitecture.BasePersenter;
import kelijun.com.androidarchitecture.BaseView;

/**
 * Created by ${kelijun} on 2018/6/22.
 */

public interface AddEditTaskContract {
    interface View extends BaseView<Persenter> {
        void showEmptyTaskError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        boolean isActive();

    }
    interface Persenter extends BasePersenter {
        void saveTask(String title, String description);

        void populateTask();

        boolean isDataMissing();
    }
}
