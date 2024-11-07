package com.example.todolistapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * DBHandler class for database communication and functions
 * Database is local and SQLite is used
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "todolistdb";
    private static final int DB_VERSION = 2;
    private static final String TABLE1_NAME = "tasks";
    private static final String TABLE2_NAME = "folders";
    private static final String ID_COL = "task_id";
    private static final String TITLE_COL = "task_title";
    private static final String FOLDER_COL = "task_folder";
    private static final String DATE_COL = "task_due_date";
    private static final String TIME_COL = "task_time_due";
    private static final String PRIORITY_COL = "task_priority";
    private static final String DONE_COL = "is_done";
    private static final String FOLDER_ID_COL = "folder_id";
    private static final String FOLDER_NAME_COL = "folder_name";
    private  static final String DEFAULT_FOLDER = "All";


    // constructor for the database handler.
    public DBHandler(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE1_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE_COL + " TEXT, "
                + FOLDER_COL + " INTEGER, "
                + PRIORITY_COL + " INTEGER, "
                + DATE_COL + " TEXT, "
                + TIME_COL + " TEXT, "
                + DONE_COL + " INTEGER, "
                + "FOREIGN KEY(" + FOLDER_COL + ") REFERENCES " + TABLE2_NAME + "(" + FOLDER_ID_COL + "))";

        String query2 = "CREATE TABLE " + TABLE2_NAME + " ("
                + FOLDER_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FOLDER_NAME_COL + " TEXT)";

        String query3 = "INSERT INTO " + TABLE2_NAME + "(" + FOLDER_NAME_COL + ") VALUES ('" +
                DEFAULT_FOLDER + "')";

        db.execSQL(query2);
        db.execSQL(query);
        db.execSQL(query3);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        onCreate(db);
    }

    /**
     * function that creates a new task in the database
     * @param title
     * @param folder
     * @param priority
     * @param due_date
     * @param time_due
     */
    public void addNewTask(String title, int folder, int priority, String due_date, String time_due) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE_COL, title);
        values.put(FOLDER_COL, folder);
        values.put(PRIORITY_COL, priority);
        values.put(DATE_COL, due_date);
        values.put(TIME_COL, time_due);
        values.put(DONE_COL, 0);
        db.insert(TABLE1_NAME, null, values);
        db.close();
    }

    /**
     * function for creating a new folder in the db
     * @param name
     */
    public void addNewFolder(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Query to get the count of folders with the same name
        String query = "SELECT COUNT(*) FROM " + TABLE2_NAME + " WHERE " + FOLDER_NAME_COL + " LIKE '" + name + "%'";
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0); // Get the count from the first column of the result
        }
        cursor.close();

        // If a folder with the same name exists, append a number to the name
        if (count > 0) {
            name += " (" + (count + 1) + ")";
        }

        values.put(FOLDER_NAME_COL, name);
        db.insert(TABLE2_NAME, null, values);
        db.close();
    }

    /**
     * function to get all tasks in the db
     * @return array list of TaskModal objects
     */
    public ArrayList<TaskModal> readTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        // create a cursor with query to read data from database.
        Cursor cursorTasks = db.rawQuery("SELECT * FROM " + TABLE1_NAME, null);

        // create a new array list.
        ArrayList<TaskModal> TaskModalArrayList = new ArrayList<TaskModal>();
        // move our cursor to first position.
        if (cursorTasks.moveToFirst()) {
            do {
                // add the data from cursor to our array list.
                TaskModalArrayList.add(new TaskModal(cursorTasks.getInt(0), //taskid
                        cursorTasks.getString(1), //title
                        cursorTasks.getInt(2), //list id
                        cursorTasks.getInt(3), //priority
                        cursorTasks.getString(4), //due date
                        cursorTasks.getString(5),
                        cursorTasks.getInt(6))); //time date
            }
            while (cursorTasks.moveToNext());
            // moving our cursor to next.
        }
        // close our cursor and return our array list.
        cursorTasks.close();
        return TaskModalArrayList;
    }

    /**
     * function to get all tasks from a specific folder using folder id
     * @param id
     * @return array list of TaskModal objects
     */
    public ArrayList<TaskModal> readSpecificTasks(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        // create a cursor with query to read data from database
        Cursor cursorTasks = db.rawQuery("SELECT * FROM " + TABLE1_NAME + " WHERE " +
                FOLDER_COL + " = " + id, null);

        // create a new array list
        ArrayList<TaskModal> TaskModalArrayList = new ArrayList<TaskModal>();
        // move cursor to first position
        if (cursorTasks.moveToFirst()) {
            do {
                // add the data from cursor to our array list
                TaskModalArrayList.add(new TaskModal(cursorTasks.getInt(0), //taskid
                        cursorTasks.getString(1), //title
                        cursorTasks.getInt(2), //list id
                        cursorTasks.getInt(3), //priority
                        cursorTasks.getString(4), //due date
                        cursorTasks.getString(5), //time date
                        cursorTasks.getInt(6))); //if its done
            }
            while (cursorTasks.moveToNext());
            // move our cursor to next
        }
        // close cursor and return our array list.
        cursorTasks.close();
        return TaskModalArrayList;
    }

    /**
     * function to search for tasks in a specific using task title
     * @param title
     * @return array list of TaskModal objects
     */
    public ArrayList<TaskModal> searchTasksByTitle(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorTasks;

        if (title.isEmpty()) {
            // If the title is empty, get all tasks
            cursorTasks = db.rawQuery("SELECT * FROM " + TABLE1_NAME, null);
        } else {
            // If the title is not empty, get tasks that match the title
            cursorTasks = db.rawQuery("SELECT * FROM " + TABLE1_NAME + " WHERE " +
                    TITLE_COL + " LIKE '%" + title + "%'", null);
        }

        ArrayList<TaskModal> TaskModalArrayList = new ArrayList<TaskModal>();
        if (cursorTasks.moveToFirst()) {
            do {
                TaskModalArrayList.add(new TaskModal(cursorTasks.getInt(0), //taskid
                        cursorTasks.getString(1), //title
                        cursorTasks.getInt(2), //list id
                        cursorTasks.getInt(3), //priority
                        cursorTasks.getString(4), //due date
                        cursorTasks.getString(5),
                        cursorTasks.getInt(6))); //time due
            } while (cursorTasks.moveToNext());
        }
        cursorTasks.close();
        return TaskModalArrayList;
    }

    /**
     * function to get all folders in the databse
     * @return array list of FolderModal objects
     */
    public ArrayList<FolderModal> readFolders() {
        SQLiteDatabase db = this.getReadableDatabase();
        // create a cursor with query to read data from database
        Cursor cursorFolders = db.rawQuery("SELECT * FROM " + TABLE2_NAME, null);

        // create a new array list
        ArrayList<FolderModal> FolderModalArrayList = new ArrayList<FolderModal>();
        // move cursor to first position
        if (cursorFolders.moveToFirst()) {
            do {
                // add the data from cursor to array list
                FolderModalArrayList.add(new FolderModal(cursorFolders.getInt(0),
                        cursorFolders.getString(1)));
            }
            while (cursorFolders.moveToNext());
            // move cursor to next
        }
        // close cursor and return array list
        cursorFolders.close();
        return FolderModalArrayList;
    }

    /**
     * function to get folder id based on folder name
     * @param fname
     * @return id integer
     */
    public int getFolderId(String fname){
        SQLiteDatabase db = this.getReadableDatabase();
        int id;
        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorFolders = db.rawQuery("SELECT " + FOLDER_ID_COL + " FROM " + TABLE2_NAME + " WHERE " +
                FOLDER_NAME_COL + " = '" + fname + "'", null);
        if (cursorFolders.moveToFirst()) {
                id = cursorFolders.getInt(0);
        }else {
            id = -1;
        }
        cursorFolders.close();
        return id;
    }

    /**
     * function to get folder name based on folder id
     * @param fId
     * @return string name
     */
    public String getFolderName(int fId){
        SQLiteDatabase db = this.getReadableDatabase();
        String name = "";
        // create a cursor with query to read data from database
        Cursor cursorFolders = db.rawQuery("SELECT " + FOLDER_NAME_COL + " FROM " + TABLE2_NAME + " WHERE " +
                FOLDER_ID_COL + " = '" + fId + "'", null);
        if (cursorFolders.moveToFirst()) {
            name = cursorFolders.getString(0);
        }
        cursorFolders.close();
        return name;
    }

    /**
     * function to update task details
     * @param task_id
     * @param title
     * @param due_date
     * @param time_due
     * @param priority
     */
    public void updateTask(int task_id, String title, String due_date,
                           String time_due, int priority, int isDone) {

        // get writable database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // pass all values along with its key and value pair.
        values.put(ID_COL, task_id);
        values.put(TITLE_COL, title);
        values.put(DATE_COL, due_date);
        values.put(TIME_COL, time_due);
        values.put(PRIORITY_COL, priority);
        values.put(DONE_COL, isDone);

        // call the update method to update database and pass values
        // compare task ids to confirm
        db.update(TABLE1_NAME, values, "task_id=?", new String[]{String.valueOf(task_id)});
        db.close();
    }

    public void updateTaskCheckbox(int task_id, int isDone) {

        // get writable database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // pass all values along with its key and value pair.
        values.put(ID_COL, task_id);
        values.put(DONE_COL, isDone);

        // call the update method to update database and pass values
        // compare task ids to confirm
        db.update(TABLE1_NAME, values, "task_id=?", new String[]{String.valueOf(task_id)});
        db.close();
    }

    /**
     * function to update folder name
     * @param id
     * @param name
     */
    public void updateFolder(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Query to get the count of folders with the same name
        String query = "SELECT COUNT(*) FROM " + TABLE2_NAME + " WHERE " + FOLDER_NAME_COL + " LIKE '" + name + "%'";
        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0); // Get the count from the first column of the result
        }
        cursor.close();

        // If a folder with the same name exists, append a number to the name
        if (count > 0) {
            name += " (" + (count + 1) + ")";
        }

        // pass all values along with its key and value pair.
        values.put(FOLDER_ID_COL, id);
        values.put(FOLDER_NAME_COL, name);

        db.update(TABLE2_NAME, values, "folder_id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * function to delete a task based on id
     * @param task_id
     */
    public void deleteTask(int task_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE1_NAME, "task_id=?", new String[]{String.valueOf(task_id)});
        db.close();
    }

    /**
     * functiont ot delete a folder
     * @param folder_id
     */
    public void deletefolder(int folder_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE2_NAME, "folder_id=?", new String[]{String.valueOf(folder_id)});
        db.delete(TABLE1_NAME, "task_folder=?", new String[]{String.valueOf(folder_id)});
        db.close();
    }

    /**
     * function to cancel a reminder set to a task
     * @param taskId
     * @param context
     */
    public void cancelReminder(int taskId, Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        //create a pendind intent including an intent to the Alarm receiver with task id as identifier
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskId, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //cancel the reminder
        alarmManager.cancel(pendingIntent);
    }

    /**
     * function to set a reminder for a task
     * @param id
     * @param title
     * @param selectedDueDate
     * @param selectedDueTime
     * @param context
     */
    public void setReminder(int id, String title, String selectedDueDate, String selectedDueTime, Context context) {
        if (!selectedDueDate.isEmpty() && !selectedDueTime.isEmpty()) { //check dates shouldnt be empty
            //if time is empty set it to 12:00 AM
            if (selectedDueTime.isEmpty()) {
                selectedDueTime = "00:00";
            }
            //if date is empty set it to today's date
            if (selectedDueDate.isEmpty()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                selectedDueDate = dateFormat.format(new Date());
            }
            try {
                //date formatters
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                Date reminderDateTime = dateFormat.parse(selectedDueDate + " " + selectedDueTime);

                //intent to alarm receiver class
                Intent intent = new Intent(context, AlarmReceiver.class);
                //pass task title with te intent
                intent.putExtra("task", title);

                //create pending intent with task id and intent
                int flags = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, flags);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                if (alarmManager != null && reminderDateTime != null) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderDateTime.getTime(), pendingIntent);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error setting reminder", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
