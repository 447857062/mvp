package kelijun.com.androidarchitecture.tasks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import kelijun.com.androidarchitecture.R;
public class TasksActivity extends AppCompatActivity {
    private TasksPersenter tasksPersenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        //create persenter
       /* tasksPersenter=new TasksPersenter(
                Injection.provideTasksRepository(getApplicationContext()), tasksFragment);*/
    }
}
