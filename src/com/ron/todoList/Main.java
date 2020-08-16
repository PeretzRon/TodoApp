package com.ron.todoList;

import com.ron.todoList.models.TodoData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Todo");
        primaryStage.getIcons().add(new Image("com/ron/todoList/resources/checklist.jpg"));
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        try {
            TodoData.getInstance().storeTodoItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        try {
            TodoData.getInstance().loadTodoItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
