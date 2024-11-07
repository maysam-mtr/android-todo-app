package com.example.todolistapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Task Activity class
 */
public class TasksActivity extends AppCompatActivity {
    private DBHandler db; //db handler object
    private int fId; //folder id to be passed from intent
    private String fName; //folder name to be passed from intent
    private RecyclerView tasksRecView; //task recycler view
    private ArrayList<TaskModal> tasks; //tasks array list to store task objects
    private Spinner order;
    private Toolbar toolbar;
    private TasksRecViewAdapter adapter; //adapter to manage tasks
    private ArrayList<TaskModal> originalTasks; //array list to be used in sorting

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_activity);
        toolbar = findViewById(R.id.toolbar);
        //set up toolbar
        setSupportActionBar(toolbar);

        //fill order by spinner
        order = findViewById(R.id.orderSp);

        //fill sorting options spinner
        String[] orderOptions = {"", "Due Date", "Priority"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, orderOptions);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        order.setAdapter(adapter1);

        //get intent extras
        Bundle extras = getIntent().getExtras();
        fId = extras.getInt("folder_id");
        fName = extras.getString("folder_name");

        // creating a new dbhandler class and passing our context to it.
        db = new DBHandler(TasksActivity.this);

        //set up toolbar title with design
        SpannableString s = new SpannableString(fName);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, fName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, fName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        //fill tasks arraylist from database
        tasksRecView = (RecyclerView) findViewById(R.id.tasksRv);
        if (fId == 1){ //if it is the main folder "all" fill with all tasks
            tasks = db.readTasks();
        }else{
            tasks = db.readSpecificTasks(fId);
        }

        //setup adapter
        adapter = new TasksRecViewAdapter(tasks, TasksActivity.this);
        tasksRecView.setAdapter(adapter);

        // Keep a copy of the original list
        originalTasks = new ArrayList<>(tasks);

        //listener to sorting options spinner
        order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOrder = (String) parent.getItemAtPosition(position);

                // Sort tasks based on the selected order
                if (selectedOrder.equals("Due Date")) {
                    // sort by due date
                    Collections.sort(tasks, new Comparator<TaskModal>() {
                        @Override
                        public int compare(TaskModal t1, TaskModal t2) {
                            if (!t1.getDueDate().isEmpty() && !t2.getDueDate().isEmpty()) {
                                String[] dateParts1 = t1.getDueDate().split("-");
                                String[] dateParts2 = t2.getDueDate().split("-");
                                Date d1 = new Date(Integer.parseInt(dateParts1[2]),
                                        Integer.parseInt(dateParts1[1]), Integer.parseInt(dateParts1[0]));
                                Date d2 = new Date(Integer.parseInt(dateParts2[2]),
                                        Integer.parseInt(dateParts2[1]), Integer.parseInt(dateParts2[0]));
                                if (d1.compareTo(d2) == 0){
                                    if (!t1.getTimeDue().isEmpty() && !t2.getTimeDue().isEmpty()) {
                                        String[] tParts1 = t1.getTimeDue().split(":");
                                        String[] tParts2 = t2.getTimeDue().split(":");
                                        int hour1 = Integer.parseInt(tParts1[0]);
                                        int hour2 = Integer.parseInt(tParts2[0]);
                                        if (hour1 < hour2){
                                            return -1; // t1 is earlier than t2
                                        } else if (hour1 > hour2) {
                                            return 1; // t1 is later than t2
                                        } else {
                                            // If the hours are equal, compare the minutes
                                            int minute1 = Integer.parseInt(tParts1[1]);
                                            int minute2 = Integer.parseInt(tParts2[1]);
                                            if (minute1 < minute2) {
                                                return -1; // t1 is earlier than t2
                                            } else if (minute1 > minute2) {
                                                return 1; // t1 is later than t2
                                            } else {
                                                return 0; // t1 and t2 are at the same time
                                            }
                                        }
                                    }
                                } else {
                                    return d1.compareTo(d2);
                                }
                            }
                            return t1.getDueDate().compareTo(t2.getDueDate());
                        }
                    });
                    adapter.setTasks(tasks);
                } else if (selectedOrder.equals("Priority")) {
                    // sort by priority
                    Collections.sort(tasks, new Comparator<TaskModal>() {
                        @Override
                        public int compare(TaskModal t1, TaskModal t2) {
                            return Integer.compare(t2.getPriority(), t1.getPriority());
                        }
                    });
                    adapter.setTasks(tasks);
                }else{
                    //refill array list with original order and refresh adapter
                    tasks.clear();
                    tasks.addAll(originalTasks);
                    adapter.setTasks(tasks);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //refill array list with original order and refresh adapter
                tasks.clear();
                tasks.addAll(originalTasks);
                adapter.notifyDataSetChanged();
            }
        });

        tasksRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * function to add task
     * @param view
     */
    public void onAddTaskClick(View view) {
        //setup dialog for taking input for creating a task
        AlertDialog.Builder builder = new AlertDialog.Builder(TasksActivity.this);
        LayoutInflater inflater = TasksActivity.this.getLayoutInflater();

        View rootView = inflater.inflate(R.layout.create_task_dialog, null);

        //get views and widgets from the dialog layout
        EditText newTaskTitle = rootView.findViewById(R.id.ttitle);
        Spinner priority = rootView.findViewById(R.id.taskPriority);
        TextView selectedDate = rootView.findViewById(R.id.date);
        TextView selectedTime = rootView.findViewById(R.id.time);
        Button date = rootView.findViewById(R.id.calbtn);
        Button time = rootView.findViewById(R.id.timebtn);

        // Populate the priority spinner
        String[] priorities = {"", "High", "Medium", "Low"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priority.setAdapter(adapter1);

        // Set up the date and time pickers
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current date
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(TasksActivity.this,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            // Display Selected date in EditText
                            selectedDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            if (selectedTime.getText().toString().isEmpty()){
                                //set initial time
                                selectedTime.setText("00:00");
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        //time picker
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                int am_pm = c.get(Calendar.AM_PM);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(TasksActivity.this,
                        (view, hourOfDay, minute) -> {
                            selectedTime.setText(hourOfDay + ":" + minute);
                            if (selectedDate.getText().toString().isEmpty()){
                                //set date to today
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                selectedDate.setText(formatter.format(calendar.getTime()));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        builder.setView(rootView);

        //on create button click
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String ttitle = newTaskTitle.getText().toString();
                String selectedPriority = priority.getSelectedItem().toString();
                String selectedDueDate = selectedDate.getText().toString();
                String selectedDueTime = selectedTime.getText().toString();

                //set priority from string to integer
                int p = 0;
                switch (selectedPriority){
                    case "High": p=3; break;
                    case"Medium": p=2; break;
                    case "Low": p=1; break;
                    default: p=0;
                }

                //incase title is empty dont create task
                if (ttitle.isEmpty()) {
                    Toast.makeText(TasksActivity.this, "Please enter title to create " +
                            "the task", Toast.LENGTH_SHORT).show();
                }else {
                    //add task to database
                    db.addNewTask(ttitle, fId, p, selectedDueDate, selectedDueTime);

                    // Update the adapter and notify of dataset change
                    tasks = db.readSpecificTasks(fId);
                    adapter.setTasks(tasks);

                    Toast.makeText(TasksActivity.this, "Task added", Toast.LENGTH_SHORT).show();

                    // Set up the alarm for the reminder
                    TaskModal t = tasks.get(tasks.size()-1);
                    db.setReminder(t.getId(), t.getTitle(), selectedDueDate, selectedDueTime, TasksActivity.this);

                    //set back the widgets and texts
                    newTaskTitle.setText("");
                    selectedDate.setText("");
                    selectedTime.setText("");
                    priority.setSelection(0);
                }
            }
        });

        //on cancel button click cancel dialog
        builder.setNegativeButton("Cancel", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * function to get back to main page
     * @param view
     */
    public void onBackClick(View view) {
        Intent i = new Intent(TasksActivity.this, MainActivity.class);
        startActivity(i);
    }

    /**
     * function to handle seach functionality
     * @param menu The options menu in which you place your items.
     *
     * @return boolean value, true if worked
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate search menu
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * get folders and refresh adapter based on the folders that match the query
             * @param query the query text that is to be submitted
             *
             * @return
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                TasksRecViewAdapter adapter = new TasksRecViewAdapter(db.searchTasksByTitle(query),
                        TasksActivity.this);
                tasksRecView.setAdapter(adapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        /**
         * when the user presses back the orginal tasks are displayed
         */
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Refresh the adapter with all tasks when the SearchView loses focus
                    TasksRecViewAdapter adapter = new TasksRecViewAdapter(tasks, TasksActivity.this);
                    tasksRecView.setAdapter(adapter);
                }
            }
        });


        return true;
    }
}
