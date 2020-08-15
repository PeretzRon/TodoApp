package com.ron.todoList.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

// singleton
public class TodoData implements Dao {
    private static final TodoData instance = new TodoData();
    private static final String filePath = "data.txt";

    private ObservableList<TodoItem> todoItems; // use observable for binding data
    private final DateTimeFormatter formatter;

    private TodoData() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public static TodoData getInstance() {
        return instance;
    }

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }

    // load data from file
    @Override
    public void loadTodoItems() throws IOException {
        todoItems = FXCollections.observableArrayList(); // FXCollection is for better performance
        Path path = Paths.get(filePath);
        BufferedReader br = Files.newBufferedReader(path);
        String input;

        try {
            while ((input = br.readLine()) != null) {
                String[] data = input.split("\t"); // The information is separated by tabs
                String shortDescription = data[0];
                String description = data[1];
                String dateString = data[2];

                LocalDate itemDateExpire = LocalDate.parse(dateString, formatter);
                todoItems.add(new TodoItem(shortDescription, description, itemDateExpire));
            }

        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    // save data to file
    @Override
    public void storeTodoItems() throws IOException {
        Path path = Paths.get(filePath);
        BufferedWriter bw = Files.newBufferedWriter(path);
        try {
            Iterator<TodoItem> iterator = todoItems.iterator();
            while (iterator.hasNext()) {
                TodoItem todoItem = iterator.next();
                bw.write(String.format("%s\t%s\t%s\t",
                        todoItem.getShortDescription(),
                        todoItem.getDescription(),
                        todoItem.getExpireDate().format(formatter)));
                bw.newLine();
            }
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }

    public void addTodoItem(TodoItem newItem) {
        todoItems.add(newItem);
    }
}
