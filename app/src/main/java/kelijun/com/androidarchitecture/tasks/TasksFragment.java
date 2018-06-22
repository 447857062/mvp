package kelijun.com.androidarchitecture.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kelijun.com.androidarchitecture.R;
import kelijun.com.androidarchitecture.addedittask.AddEditTaskActivity;
import kelijun.com.androidarchitecture.data.Task;
import kelijun.com.androidarchitecture.taskdetail.TaskDetailActivity;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ${kelijun} on 2018/6/21.
 */

public class TasksFragment extends Fragment implements TasksContract.View {
    private TasksContract.Persenter mPersenter;

    private TasksAdapter mListAdapter;

    private TextView mFilteringLabelView;

    private LinearLayout mTasksView;


    private View mNoTasksView;

    private ImageView mNoTaskIcon;

    private TextView mNoTaskMainView;

    private TextView mNoTaskAddView;

    TaskItemListener mTaskItemListener = new TaskItemListener() {
        @Override
        public void onTaskClick(Task clickedTask) {
            mPersenter.openTaskDetails(clickedTask);

        }

        @Override
        public void onCompleteTaskClick(Task completedTask) {
            mPersenter.completeTask(completedTask);
        }

        @Override
        public void onActivtedTaskClick(Task activtedTsak) {
            mPersenter.activeTask(activtedTsak);
        }
    };

    public TasksFragment() {
        // Requires empty public constructor
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mListAdapter = new TasksAdapter(new ArrayList<Task>(0), mTaskItemListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPersenter.result(requestCode, resultCode);
    }


    @Override
    public void onResume() {
        super.onResume();
        mPersenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tasks_frag, container, false);
        ListView listView = rootView.findViewById(R.id.tasks_list);
        listView.setAdapter(mListAdapter);

        mFilteringLabelView = rootView.findViewById(R.id.filteringLabel);
        mTasksView = rootView.findViewById(R.id.tasksLL);

        mNoTasksView = rootView.findViewById(R.id.noTasks);
        mNoTaskIcon = rootView.findViewById(R.id.noTasksIcon);
        mNoTaskMainView = rootView.findViewById(R.id.noTasksMain);
        mNoTaskAddView = rootView.findViewById(R.id.noTasksAdd);

        mNoTaskAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTask();
            }
        });

        FloatingActionButton fab = (getActivity()).findViewById(R.id.fab_add_task);

        fab.setImageResource(R.drawable.ic_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPersenter.addNewTask();

            }
        });
        ScrollChildSwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        swipeRefreshLayout.setScrollUpChild(listView);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPersenter.loadTasks(false);
            }
        });
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mPersenter.clearCompletedTasks();
                break;
            case R.id.menu_filter:
                showFilterPopUpMenu();
                break;
            case R.id.menu_refresh:
                mPersenter.loadTasks(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu);
    }

    @Override
    public void setPresenter(TasksContract.Persenter presenter) {
        mPersenter = checkNotNull(presenter);
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = (getView()).findViewById(R.id.refresh_layout);

        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showTasks(List<Task> tasks) {
        mListAdapter.replaceData(tasks);

        mTasksView.setVisibility(View.VISIBLE);
        mNoTasksView.setVisibility(View.GONE);
    }

    @Override
    public void showAddTask() {

       Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK);
    }

    @Override
    public void showTaskDetailsUi(String taskId) {

       Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId);
        startActivity(intent);
    }

    @Override
    public void showTaskMarkedComplete() {
        showMessage(getString(R.string.task_marked_complete));
    }

    @Override
    public void showTaskMarkedActive() {
        showMessage(getString(R.string.task_marked_active));
    }

    @Override
    public void showCompltedTaskCleared() {
        showMessage(getString(R.string.completed_tasks_cleared));
    }

    @Override
    public void showLoadingTasksError() {
        showMessage(getString(R.string.loading_tasks_error));
    }

    @Override
    public void showNoTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        );
    }

    @Override
    public void showActiveFilterLable() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_active));
    }

    @Override
    public void showCompleteFilterLable() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_completed));
    }

    @Override
    public void showAllFilterLable() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_all));
    }

    @Override
    public void showNoActiveTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_active),
                R.drawable.ic_check_circle_24dp,
                false);
    }

    private void showNoTasksViews(String mainText, int iconRes, boolean showAddView) {
        mTasksView.setVisibility(View.GONE);
        mNoTasksView.setVisibility(View.VISIBLE);

        mNoTaskMainView.setText(mainText);
        mNoTaskIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoTaskAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showNoCompletedTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_completed),
                R.drawable.ic_verified_user_24dp,
                false
        );
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_task_message));
    }
    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }
    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showFilterPopUpMenu() {
        PopupMenu popupMenu = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popupMenu.getMenuInflater().inflate(R.menu.filter_tasks, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.active:
                        mPersenter.setFiltering(TasksFilterType.ACTIVE_TASKS);
                        break;
                    case R.id.completed:
                        mPersenter.setFiltering(TasksFilterType.COMPLETED_TASKS);
                        break;
                    default:
                        mPersenter.setFiltering(TasksFilterType.ALL_TASKS);
                        break;
                }
                mPersenter.loadTasks(false);
                return true;
            }
        });

        popupMenu.show();
    }

    private static class TasksAdapter extends BaseAdapter {
        private List<Task> mTasks;
        private TaskItemListener mItemListener;

        public TasksAdapter(List<Task> mTasks, TaskItemListener mItemListener) {
            setList(mTasks);
            this.mItemListener = mItemListener;
        }

        private void setList(List<Task> mTasks) {
            this.mTasks = checkNotNull(mTasks);
        }

        public void replaceData(List<Task> tasks) {
            setList(tasks);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTasks.size();
        }

        @Override
        public Task getItem(int i) {
            return mTasks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rootView = view;
            if (rootView == null) {
                rootView = LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.task_item, viewGroup, false);
            }
            final Task task = getItem(i);
            TextView titleTV = rootView.findViewById(R.id.title);
            titleTV.setText(task.getTitleForList());

            CheckBox completeCB = rootView.findViewById(R.id.complete);

            completeCB.setChecked(task.isCompleted());
            if (task.isCompleted()) {
                rootView.setBackgroundDrawable(viewGroup.getContext().getResources().getDrawable(R.drawable.list_completed_touch_feedback));

            } else {
                rootView.setBackgroundDrawable(viewGroup.getContext().getResources().getDrawable(R.drawable.touch_feedback));

            }

            completeCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!task.isCompleted()) {
                        mItemListener.onCompleteTaskClick(task);

                    } else {
                        mItemListener.onActivtedTaskClick(task);
                    }
                }
            });

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onTaskClick(task);
                }
            });
            return rootView;
        }
    }

    private interface TaskItemListener {
        void onTaskClick(Task clickedTask);

        void onCompleteTaskClick(Task completedTask);

        void onActivtedTaskClick(Task activtedTsak);
    }

}
