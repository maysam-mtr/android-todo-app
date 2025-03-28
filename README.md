# Android-based Task Management App

This is a powerful, flexible, and easy-to-use application developed in Android Studio with the help of Java and XML. Its goal is to help you manage your tasks and time more effectively. The user-friendly interface makes it easy for anyone to generate, update, and manage tasks.

This app is a comprehensive task management solution that offers a wide range of features to help you organize your tasks and manage your time more effectively. Whether you have multiple projects to manage at work, household chores to keep track of, or just want to keep track of your to-do list on a daily basis, this app has everything you need to manage everything efficiently.
# Functionalities:
**Task Management**: Users can create new tasks, set their priority, and assign them to different categories or lists. Each task can have a title, description, due date, and priority level. Tasks can be edited or deleted as needed.

**Task Sorting***: The app provides several sorting options, allowing users to view their tasks in the order that makes the most sense for them. They can sort tasks by due date or priority.
Task Search: A user can also search for the task that he created by title or words.

**Reminders and Notifications**: If the user chooses date and time for a task, a notification reminder is automatically set when the task is created. The app will send a notification at the specified time, ensuring that users never forget about their important tasks. If the user only chooses a date the time is automatically set to 12:00AM, and if the user only chooses a time, the date is automatically set to today. Additionally, if a user updates the time of a task, the reminder is updated, and if the user deletes a task, the reminder is cancelled. The app uses the Android AlarmManager service to schedule reminders. This allows reminders to be delivered even if the app is not currently running.

**Multiple Task Lists**: Users can create multiple task lists, making it easy to separate personal tasks from work tasks, or to organize tasks for different projects. Users can also rename and delete these lists, and there is a main list (“All”) that is created by default and cannot be deleted or renamed as it contains all tasks made by the user.

**Data Persistence**: The app uses SQLite for data persistence, so all tasks and lists remain saved even when the app is closed, or the device is restarted.
User-friendly Interface: The app features a clean, intuitive interface that makes it easy to navigate and manage tasks. It uses Recycler View for efficient display of tasks.
