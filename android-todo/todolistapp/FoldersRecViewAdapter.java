package com.example.todolistapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Folder Recycler View Adapter class
 */
public class FoldersRecViewAdapter extends RecyclerView.Adapter<FoldersRecViewAdapter.ViewHolder>{

    private ArrayList<FolderModal> folders = new ArrayList<>(); //folders array list
    private Context context; //context to be passed
    private int position; //position of item in adapter

    public FoldersRecViewAdapter() {

    }

    /**
     * arg construtor
     * @param foldersList
     * @param context
     */
    public FoldersRecViewAdapter(ArrayList<FolderModal> foldersList, Context context) {
        this.folders = foldersList;
        this.context = context;
    }


    /**
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return view holder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folders_list_item, parent, false);
        return new ViewHolder(view);

    }

    /**
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.folderName.setText(folders.get(position).getFolder_name());

        final int finalPosition = position; // final copy of position

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to the tasks activity
                Intent i = new Intent(context, TasksActivity.class);
                i.putExtra("folder_id", folders.get(finalPosition).getFolder_id());
                i.putExtra("folder_name", folders.get(finalPosition).getFolder_name());
                context.startActivity(i);
            }
        });
    }

    /**
     * item count getter
     * @return integer number of items
     */
    @Override
    public int getItemCount() {
        return folders.size();
    }

    /**
     * setter for the array list folders with refreshing the adapter
     * @param folders
     */
    public void setFolders(ArrayList<FolderModal> folders) {
        this.folders = folders;
        notifyDataSetChanged(); //notify recycler view of any data change so it refreshes
    }

    /**
     * position getter
     * @return integer
     */
    public int getPosition() {
        return position;
    }

    /**
     * position setter
     * @param position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * View holder class for handling individual item in adapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnCreateContextMenuListener, View.OnLongClickListener {
        private TextView folderName; //text view from layout of adapter item
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(this);
            folderName = itemView.findViewById(R.id.folderName);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            Activity activity = (Activity) v.getContext();
            //inflate context menu for folders recycler view
            MenuInflater inflater = activity.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }

        @Override
        public boolean onLongClick(View v) {
            //set the context menu to show on long click
            setPosition(getAdapterPosition());
            return false;
        }
    }
}
