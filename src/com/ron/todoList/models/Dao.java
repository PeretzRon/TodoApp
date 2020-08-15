package com.ron.todoList.models;

import java.io.IOException;

public interface Dao {

    void loadTodoItems() throws IOException;

    void storeTodoItems() throws IOException;

    void addNewItem(TodoItem item);

    void deleteItem(TodoItem item);

    void editItem(TodoItem item);

}
