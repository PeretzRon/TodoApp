package com.ron.todoList;

import com.ron.todoList.models.TodoData;
import com.ron.todoList.models.TodoItem;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    @FXML
    private ListView<TodoItem> todoListView;

    @FXML
    private TextArea itemDetailsTextArea;

    @FXML
    private Label deadlineLabel;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ContextMenu listContextMenu;

    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<TodoItem> filteredList;
    private Predicate<TodoItem> wantAllItems;
    private Predicate<TodoItem> todayItems;

    public void editItemWrapper() {
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
        editItem(item);
    }

    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REMOVE));
        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.EDIT));
        deleteMenuItem.setOnAction(actionEvent -> {
            TodoItem item = todoListView.getSelectionModel().getSelectedItem();
            deleteItem(item);
        });

        editMenuItem.setOnAction(actionEvent -> {
            editItemWrapper();
        });
        listContextMenu.getItems().addAll(deleteMenuItem);
        listContextMenu.getItems().addAll(editMenuItem);


        // eventListener click on item -> load the data to textArea and expire date label
        todoListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                TodoItem todoItem = todoListView.getSelectionModel().getSelectedItem();
                itemDetailsTextArea.setText(todoItem.getDescription());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                deadlineLabel.setText(formatter.format(todoItem.getExpireDate()));
            }

        });

        wantAllItems = todoItem -> true;
        todayItems = todoItem -> todoItem.getExpireDate().equals(LocalDate.now());

        filteredList = new FilteredList<>(TodoData.getInstance().getTodoItems(), wantAllItems);

        SortedList<TodoItem> sortedList = new SortedList<>(filteredList,
                Comparator.comparing(TodoItem::getExpireDate));
        todoListView.setItems(sortedList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); // user can select only one
        todoListView.getSelectionModel().selectFirst(); // when load first time show the first item

        todoListView.setCellFactory(todoItemListView -> {
            ListCell<TodoItem> cell = new ListCell<>() {
                @Override
                protected void updateItem(TodoItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.getShortDescription());
                        if (item.getExpireDate().isBefore(LocalDate.now())) {
                            // set color red for items that their date is pass
                            setTextFill(Color.RED);
                        } else {
                            setTextFill(Color.BLACK);
                        }
                    }
                }
            };

            // add to each cell contextMenu
            cell.emptyProperty().addListener(
                    (obs, wasEmpty, isNowEmpty) -> {
                        if (isNowEmpty) {
                            cell.setContextMenu(null);
                        } else {
                            cell.setContextMenu(listContextMenu);
                        }

                    });
            return cell;
        });

    }

    @FXML
    public void showNewItemDialog() {
        addEditItemDialog(false, null);
    }

    private void editItem(TodoItem item) {
        addEditItemDialog(true, item);
    }

    private void deleteItem(TodoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete item: " + item.getShortDescription());
        alert.setContentText("Are you sure?  Press OK to confirm, or cancel to Back out.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            TodoData.getInstance().deleteItem(item);
        }
    }

    private void addEditItemDialog(boolean isEdit, TodoItem item) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle(isEdit ? "Edit Item: " + item.getShortDescription() : "Add New Todo Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml")); // load dialog (add item) window
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        DialogController controller = fxmlLoader.getController();
        if (isEdit) {
            // load the current date to fields
            controller.getShortDescriptionField().setText(item.getShortDescription());
            controller.getDetailsArea().setText(item.getDescription());
            controller.getDeadlinePicker().setValue(item.getExpireDate());
            controller.setId(item.getId());
        }

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // user press OK
            TodoItem newItem = controller.processResults(isEdit);
            todoListView.getSelectionModel().select(newItem);
        }
    }

    // exit button from the menuBar
    @FXML
    public void onExitHandler(ActionEvent actionEvent) {
        Platform.exit();
    }

    // delete item when delete key been pressed
    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
        if (item != null) {
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(item);
            }
        }
    }

    @FXML
    public void onFilterButtonHandler(ActionEvent actionEvent) {
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
        if (item != null) {
            if (filterToggleButton.isSelected()) {
                filteredList.setPredicate(todayItems);
                if (filteredList.isEmpty()) {
                    itemDetailsTextArea.clear();
                    deadlineLabel.setText("");
                } else if (filteredList.contains(item)) {
                    todoListView.getSelectionModel().select(item);
                } else {
                    todoListView.getSelectionModel().selectFirst();
                }
            } else {
                filteredList.setPredicate(wantAllItems);
                todoListView.getSelectionModel().select(item);
            }
        }

    }
}
