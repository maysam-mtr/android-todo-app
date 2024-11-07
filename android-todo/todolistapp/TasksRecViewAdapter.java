package com.example.todolistapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class TasksRecViewAdapter extends RecyclerView.Adapter<TasksRecViewAdapter.ViewHolder>{

    private ArrayList<TaskModal> tasks = new ArrayList<>(); //tasks array list
    private Context context; //context to be passed
    DBHandler db; //database handler object
    public TasksRecViewAdapter() {

    }

    /**
     * arg contructor that takes array list of tasks and the context
     * @param tasksList
     * @param context
     */
    public TasksRecViewAdapter(ArrayList<TaskModal> tasksList, Context context) {
        this.tasks = tasksList;
        this.context = context;
        this.db = new DBHandler(context);
    }

    /**
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @NonNull
    @Override
    public TasksRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasks_list_item, parent, false);
        return new TasksRecViewAdapter.ViewHolder(view);
    }

    /**
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull TasksRecViewAdapter.ViewHolder holder, int position) {
        //get the task chosen by the user
        TaskModal chosenTask = tasks.get(holder.getAdapterPosition());

        //fill the display of the task details
        holder.taskTitle.setText(chosenTask.getTitle());
        holder.taskCategory.setText(db.getFolderName(chosenTask.getList()));
        if (chosenTask.getIsDone() == 1){
            holder.checked.setChecked(true);
        }else {
            holder.checked.setChecked(false);
        }

        //if time is empty set the display to invisible otherwise fill time detail
        if(chosenTask.getTimeDue().isEmpty()){
            holder.txttime.setVisibility(View.INVISIBLE);

        }else {
            holder.taskTime.setText(chosenTask.getTimeDue());
            holder.txttime.setVisibility(View.VISIBLE);
        }

        //if date is empty set the display to invisible otherwise fill date detail
        if (chosenTask.getDueDate().isEmpty()){
            holder.txtdate.setVisibility(View.INVISIBLE);
        }else {
            holder.taskDate.setText(chosenTask.getDueDate());
            holder.txtdate.setVisibility(View.VISIBLE);
        }

        //display priority choice
        if (chosenTask.getPriority() == 1){
            holder.taskPriority.setText("!");
        }else if (chosenTask.getPriority() == 2){
            holder.taskPriority.setText("!!");
        }else if (chosenTask.getPriority() == 3){
            holder.taskPriority.setText("!!!");
        }else{
            holder.taskPriority.setText("");
        }

        //on delete button click
        holder.deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a dialog to confirm with user deleting the task
                new AlertDialog.Builder(context)
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete this task?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //cancel reminder
                                if (!chosenTask.getDueDate().isEmpty() || !chosenTask.getTimeDue().isEmpty()){
                                    db.cancelReminder(chosenTask.getId(), context);
                                }

                                //delete task from db and refresh adapter
                                db.deleteTask(chosenTask.getId());
                                tasks.remove(holder.getAdapterPosition());
                                notifyDataSetChanged();
                                Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });

        //on edit button click
        holder.editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create dialog for user to input edit
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();

                View rootView = inflater.inflate(R.layout.create_task_dialog, null);

                //get widgets and views from dialog layout
                EditText newTaskTitle = rootView.findViewById(R.id.ttitle);
                Spinner priority = rootView.findViewById(R.id.taskPriority);
                TextView selectedDate = rootView.findViewById(R.id.date);
                TextView selectedTime = rootView.findViewById(R.id.time);
                Button date = rootView.findViewById(R.id.calbtn);
                Button time = rootView.findViewById(R.id.timebtn);

                TextView dialogTitle = rootView.findViewById(R.id.dialogtitle);
                dialogTitle.setText("Edit Task");

                // Populate the priority spinner
                String[] priorities = {" ", "High", "Medium", "Low"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_item, priorities);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                priority.setAdapter(adapter);

                //set chosen values prior by user
                if (chosenTask.getPriority() == 3){
                    priority.setSelection(1);
                } else if (chosenTask.getPriority() == 2) {
                    priority.setSelection(2);
                } else if (chosenTask.getPriority() == 1) {
                    priority.setSelection(3);
                }else {
                    priority.setSelection(0);
                }

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
                        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                                (view, year, monthOfYear, dayOfMonth) -> {
                                    // Display Selected date in EditText
                                    selectedDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                    }
                });

                selectedDate.setText(chosenTask.getDueDate());
                selectedTime.setText(chosenTask.getTimeDue());
                newTaskTitle.setText(chosenTask.getTitle());

                time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        int mHour = c.get(Calendar.HOUR_OF_DAY);
                        int mMinute = c.get(Calendar.MINUTE);
                        int am_pm = c.get(Calendar.AM_PM);

                        // Launch Time Picker Dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                                (view, hourOfDay, minute) -> {
                                    selectedTime.setText(hourOfDay + ":" + minute);
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                });
                builder.setView(rootView);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ttitle = newTaskTitle.getText().toString();
                        String selectedPriority = priority.getSelectedItem().toString();
                        String selectedDueDate = selectedDate.getText().toString();
                        String selectedDueTime = selectedTime.getText().toString();

                        //set priority
                        int p = 0;
                        switch (selectedPriority){
                            case "High": p=3; break;
                            case"Medium": p=2; break;
                            case "Low": p=1; break;
                            default: p=0;
                        }

                        if (ttitle.isEmpty()) {
                            Toast.makeText(context, "Please enter title to create " +
                                    "the task", Toast.LENGTH_SHORT).show();
                        }else {

                            Toast.makeText(context, "Task updated", Toast.LENGTH_SHORT).show();

                            //reset reminder
                            if (!selectedDueDate.isEmpty() || !selectedDueTime.isEmpty()){
                                if (!chosenTask.getDueDate().isEmpty() || !chosenTask.getTimeDue().isEmpty()) {
                                    db.cancelReminder(chosenTask.getId(), context); // Cancel any existing reminder
                                }
                                db.setReminder(chosenTask.getId(), chosenTask.getTitle(), selectedDueDate,
                                        selectedDueTime, context); // Set a new reminder
                            }

                            //update task in database
                            db.updateTask(chosenTask.getId(), ttitle, selectedDueDate,
                                    selectedDueTime, p, chosenTask.getIsDone());
                            tasks.get(holder.getAdapterPosition()).setTask(ttitle, p, selectedDueDate,
                                    selectedDueTime, chosenTask.getIsDone());
                            notifyDataSetChanged();

                            //set back the widgets and texts
                            newTaskTitle.setText("");
                            selectedDate.setText("");
                            selectedTime.setText("");
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    db.updateTaskCheckbox(chosenTask.getId(), 1);
                }else {
                    db.updateTaskCheckbox(chosenTask.getId(), 0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(ArrayList<TaskModal> tasks){
        this.tasks = tasks;
        notifyDataSetChanged(); //notify recycler view of any data change so it refreshes
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView taskTitle, taskPriority, taskCategory, taskDate, taskTime;
        private ImageView editbtn, deletebtn;
        private TextView txtdate, txttime;
        private CheckBox checked;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            taskTitle = itemView.findViewById(R.id.txtShowTitle);
            taskPriority = itemView.findViewById(R.id.priority);
            taskCategory = itemView.findViewById(R.id.txtShowCategory);
            taskDate = itemView.findViewById(R.id.txtShowDate);
            taskTime = itemView.findViewById(R.id.txtShowTime);
            editbtn = itemView.findViewById(R.id.editbtn);
            deletebtn = itemView.findViewById(R.id.deletebtn);
            txtdate = itemView.findViewById(R.id.textDate);
            txttime = itemView.findViewById(R.id.textTime);
            checked = itemView.findViewById(R.id.check);
        }
    }
}
