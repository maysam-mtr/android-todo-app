package com.example.todolistapp;

/**
 * FolderModal class to handle Folder objects
 */
public class FolderModal {
    private int folder_id; //folder id
    private String folder_name; //folder name

    /**
     * arg-constructor that takes all variables
     * @param folder_id
     * @param folder_name
     */
    public FolderModal(int folder_id, String folder_name) {
        setFolder_id(folder_id);
        setFolder_name(folder_name);
    }

    /**
     * folder id getter
     * @return integer id
     */
    public int getFolder_id() {
        return folder_id;
    }

    /**
     * folder id setter
     * @param folder_id
     */
    public void setFolder_id(int folder_id) {
        this.folder_id = folder_id;
    }

    /**
     * folder name getter
     * @return String name
     */
    public String getFolder_name() {
        return folder_name;
    }

    /**
     * folder name setter
     * @param folder_name
     */
    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }
}
