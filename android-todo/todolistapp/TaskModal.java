package com.example.todolistapp;

/**
 * TaskModal class for handling Task Objects
 */
public class TaskModal {
    private int id; //task id
    private String title; //task title
    private String dueDate; //task due date
    private String timeDue; //task time due
    private int list_id; //folder id
    private int priority; //priority level
    private int isDone = 0; //if the task is done

    /**
     * arg constructor that takes all variables
     * @param id
     * @param title
     * @param list_id
     * @param priority
     * @param dueDate
     * @param timeDue
     */
    public TaskModal(int id, String title, int list_id, int priority,  String dueDate,
                     String timeDue, int isDone) {
        setId(id);
        setTitle(title);
        setList(list_id);
        setDueDate(dueDate);
        setTimeDue(timeDue);
        setPriority(priority);
        setIsDone(isDone);
    }

    /**
     * setter for all variables
     * @param title
     * @param priority
     * @param dueDate
     * @param timeDue
     */
    public void setTask(String title, int priority,  String dueDate, String timeDue, int isDone){
        setTitle(title);
        setDueDate(dueDate);
        setTimeDue(timeDue);
        setPriority(priority);
        setIsDone(isDone);
    }

    /**
     * id setter
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * id getter
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * title getter
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * title setter
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * due date getter
     * @return
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * due date setter
     * @param due_date
     */
    public void setDueDate(String due_date) {
        this.dueDate = due_date;
    }

    /**
     * priority getter
     * @return integer
     */
    public int getPriority() {
        return priority;
    }

    /**
     * priority setter
     * @param priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * time due getter
     * @return String
     */
    public String getTimeDue() {
        return timeDue;
    }

    /**
     * time due setter
     * @param timeDue
     */
    public void setTimeDue(String timeDue) {
        this.timeDue = timeDue;
    }

    /**
     * list/folder id getter
     * @return integer
     */
    public int getList() {
        return list_id;
    }

    /**
     * list id setter
     * @param list
     */
    public void setList(int list) {
        this.list_id = list;
    }

    public int getIsDone() {
        return isDone;
    }

    public void setIsDone(int isDone) {
        if (isDone > 1 || isDone < 0){

        }else {
            this.isDone = isDone;
        }
    }
}
