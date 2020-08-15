package com.ron.todoList;

import com.ron.todoList.models.TodoData;
import com.ron.todoList.models.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {

    @FXML
    private TextField shortDescriptionField;

    @FXML
    private TextArea detailsArea;

    @FXML
    private DatePicker deadlinePicker;

    private String id;

    public TextField getShortDescriptionField() {
        return shortDescriptionField;
    }

    public TextArea getDetailsArea() {
        return detailsArea;
    }

    public DatePicker getDeadlinePicker() {
        return deadlinePicker;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TodoItem processResults(Boolean isEdit) {
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadlineValue = deadlinePicker.getValue();

        TodoItem item;
        if (!isEdit) {
            item = new TodoItem(shortDescription, details, deadlineValue);
            TodoData.getInstance().addNewItem(item);
        } else {
            item = new TodoItem(shortDescription, details, deadlineValue, id);
            TodoData.getInstance().editItem(item);
        }
        return item;
    }
}
