package com.example.todolistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.example.todolistapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 101; // Arbitrary integer value
    private RecyclerView foldersRecView; //recycler view to display folders
    private DBHandler db; //database handler object for db communication
    private FoldersRecViewAdapter adapter; //folder adapter for the folder recycler view
    private ArrayList<FolderModal> folders; // array list for storing the folder objects from db

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create notification channel
        createNotificationChannel();

        foldersRecView = (RecyclerView) findViewById(R.id.foldersRv);

        // creating a new dbhandler class and passing our context to it.
        db = new DBHandler(MainActivity.this);

        //get folders from database and fill folder recycler view
        folders = db.readFolders();
        adapter = new FoldersRecViewAdapter(folders, MainActivity.this);
        foldersRecView.setAdapter(adapter);

        //set recycler view layout manager
        foldersRecView.setLayoutManager(new LinearLayoutManager(this));

        //register the recycler view for context menu
        registerForContextMenu(foldersRecView);

    }

    /**
     * function to create a new folder and add it to the database
     * @param view
     */
    public void onAddFolderClick(View view) {
        /**
         * create a dialog box where the user will input folder name
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View rootView = inflater.inflate(R.layout.create_folder_dialogue, null);

        final EditText newFolderName = rootView.findViewById(R.id.fname);

        builder.setView(rootView);

        //on create button click
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String fname = newFolderName.getText().toString();

                //check if name is empty
                if (fname.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a name to create " +
                            "the folder", Toast.LENGTH_SHORT).show();
                }else {
                    //add folder to db
                    db.addNewFolder(fname);

                    // Re-fetch the data from the database
                    folders = db.readFolders();

                    // Update the adapter and notify of dataset change
                    adapter = new FoldersRecViewAdapter(folders, MainActivity.this);
                    foldersRecView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(MainActivity.this, "Folder has been added.", Toast.LENGTH_SHORT).show();
                    newFolderName.setText(""); //reset edit text
                }
            }
        });

        /**
         * on cancel button click
         */
        builder.setNegativeButton("Cancel", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "No Folder created", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

        //show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * function for the context menu set on folder item of the folder recycler view
     * @param item The context menu item that was selected.
     * @return true if it worked, false otherwise
     */
    @Override
    public boolean onContextItemSelected(MenuItem item){
        //get position of item selected
        int position = adapter.getPosition();

        /**
         * on delete folder option selection
         */
        if (item.getItemId() == R.id.delete){
            //create a dialog box to let user confirm deletion of the folder
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Delete List")
                    .setMessage("Are you sure you want to delete this list? All its tasks will also be deleted.")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //get Folder object selected
                            FolderModal folder = folders.get(position);
                            if (folder.getFolder_id() == 1){
                                //incase user tried to delete the main folder "All"
                                Toast.makeText(MainActivity.this, "Cannot delete main " +
                                        "folder", Toast.LENGTH_LONG).show();
                            }else {
                                //delete folder from database and refresh adapter
                                db.deletefolder(folder.getFolder_id());
                                folders = db.readFolders();
                                adapter.setFolders(folders);
                                Toast.makeText(MainActivity.this, "Folder deleted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
        } else if (item.getItemId() == R.id.changename) {
            /** change folder name option **/

            //create dialog box that takes the new name
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
            View rootView = inflater.inflate(R.layout.edit_folder_dialog, null);

            final EditText newFolderName = rootView.findViewById(R.id.fname);

            builder.setView(rootView);

            /**
             * on change button click
             */
            builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //get folder object that is to be updated
                    FolderModal folder = folders.get(position);
                    if (folder.getFolder_id() == 1){
                        //prevent user from changing the name of the main folder "All"
                        Toast.makeText(MainActivity.this, "Cannot change main folder name",
                                Toast.LENGTH_LONG).show();
                    }else if (newFolderName.getText().toString().isEmpty()){
                        //incase the user didnt enter a name
                        Toast.makeText(MainActivity.this, "Enter a name to change list name",
                                Toast.LENGTH_LONG).show();

                    }else{
                        //update name in database and refresh adapter
                        db.updateFolder(folder.getFolder_id(), newFolderName.getText().toString());
                        folders = db.readFolders();
                        adapter.setFolders(folders);

                    }
                }
            });

            //on Cancel button click
            builder.setNegativeButton("Cancel", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            return false;
        }
        return true;
    }

    /**
     * function to create a notification channel for the project for the reminder feature
     */
    public void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder Channel";
            String description = "Channel for task reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("todoandroid", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request permission from the user
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS); // Arbitrary integer value
            }
        }
    }

}