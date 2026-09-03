package yokohama;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import yokohama.storage.Storage;
import yokohama.task.Deadline;
import yokohama.task.Event;
import yokohama.task.Task;
import yokohama.task.Todo;
import yokohama.utils.DateTimeHandler;

/**
 * Provides a Telegram-inspired graphical interface for Yokohama.
 */
public class Main extends Application {
    private static final String FILE_PATH = "data/todo_data.txt";

    private final ArrayList<Todo> tasks = new ArrayList<>();
    private final Storage storage = new Storage();
    private final VBox messages = new VBox(10);
    private final ScrollPane messagePane = new ScrollPane(messages);
    private final TextField commandField = new TextField();

    @Override
    public void start(Stage stage) {
        loadTasks();
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app");
        root.setLeft(createSidebar());
        root.setCenter(createChat());

        Scene scene = new Scene(root, 1000, 680);
        scene.getStylesheets().add(getClass().getResource("/yokohama/style.css").toExternalForm());
        stage.setTitle("Yokohama");
        stage.setMinWidth(760);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> saveTasks());
        stage.show();

        addMessage("Welcome to Yokohama! Your personal task assistant is online.", false);
        addMessage("Try: todo buy milk\n"
                + "Or: deadline submit report /by 9/3/2026 2359\n"
                + "Use list, mark <number>, unmark <number>, delete <number>, or find <word>.", false);
    }

    private VBox createSidebar() {
        Label logo = new Label("✈  Yokohama");
        logo.getStyleClass().add("logo");
        Label search = new Label("⌕  Search");
        search.getStyleClass().add("search");
        Label taskChat = new Label("✓  My Tasks\n     personal task assistant");
        taskChat.getStyleClass().add("chat-item");
        Label hint = new Label("Your saved tasks are kept\nlocally on this device.");
        hint.getStyleClass().add("sidebar-hint");
        return new VBox(18, logo, search, taskChat, hint);
    }

    private BorderPane createChat() {
        Label title = new Label("My Tasks");
        title.getStyleClass().add("chat-name");
        Label status = new Label("online");
        status.getStyleClass().add("chat-status");
        VBox header = new VBox(2, title, status);
        header.getStyleClass().add("chat-header");

        messages.getStyleClass().add("messages");
        messagePane.setFitToWidth(true);
        messagePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messagePane.getStyleClass().add("message-pane");

        commandField.setPromptText("Message Yokohama…");
        commandField.setOnAction(event -> sendCommand());
        HBox.setHgrow(commandField, Priority.ALWAYS);
        Button sendButton = new Button("Send");
        sendButton.getStyleClass().add("send-button");
        sendButton.setOnAction(event -> sendCommand());
        HBox composer = new HBox(10, commandField, sendButton);
        composer.setAlignment(Pos.CENTER);
        composer.getStyleClass().add("composer");

        BorderPane chat = new BorderPane();
        chat.setTop(header);
        chat.setCenter(messagePane);
        chat.setBottom(composer);
        return chat;
    }

    private void sendCommand() {
        String command = commandField.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        addMessage(command, true);
        commandField.clear();
        addMessage(handleCommand(command), false);
    }

    private String handleCommand(String input) {
        String[] parts = input.split("\\s+", 2);
        String action = parts[0].toLowerCase();
        String payload = parts.length == 2 ? parts[1].trim() : "";
        try {
            return switch (action) {
                case "todo" -> addTodo(payload);
                case "deadline" -> addDeadline(payload);
                case "event" -> addEvent(payload);
                case "list" -> listTasks();
                case "find" -> findTasks(payload);
                case "mark" -> changeTask(payload, true);
                case "unmark" -> changeTask(payload, false);
                case "delete" -> deleteTask(payload);
                case "exit" -> {
                    saveTasks();
                    Platform.exit();
                    yield "Your tasks are saved. See you next time!";
                }
                default -> "I don't recognise that command. Try todo, list, deadline, or event.";
            };
        } catch (Exception exception) {
            return "⚠ " + exception.getMessage();
        }
    }

    private String addTodo(String description) {
        require(!description.isEmpty(), "A todo cannot be empty.");
        tasks.add(new Task(description, false));
        saveTasks();
        return "Added a task:\n" + tasks.getLast();
    }

    private String addDeadline(String payload) {
        String[] details = payload.split(" /by ", 2);
        require(details.length == 2 && !details[0].isBlank() && !details[1].isBlank(),
                "Use: deadline <description> /by M/d/yyyy HHmm");
        LocalDateTime by = DateTimeHandler.convertToLocalDateTime(details[1].trim());
        tasks.add(new Deadline(details[0].trim(), false, by));
        saveTasks();
        return "Added a deadline:\n" + tasks.getLast();
    }

    private String addEvent(String payload) {
        int fromIndex = payload.indexOf(" /from ");
        int toIndex = payload.indexOf(" /to ");
        require(fromIndex > 0 && toIndex > fromIndex,
                "Use: event <description> /from M/d/yyyy HHmm /to M/d/yyyy HHmm");
        LocalDateTime from = DateTimeHandler.convertToLocalDateTime(payload.substring(fromIndex + 7, toIndex).trim());
        LocalDateTime to = DateTimeHandler.convertToLocalDateTime(payload.substring(toIndex + 5).trim());
        tasks.add(new Event(payload.substring(0, fromIndex).trim(), false, from, to));
        saveTasks();
        return "Added an event:\n" + tasks.getLast();
    }

    private String listTasks() {
        if (tasks.isEmpty()) {
            return "🎉 Your list is empty. Time to relax!";
        }
        StringBuilder result = new StringBuilder("Your tasks:\n");
        for (int index = 0; index < tasks.size(); index++) {
            result.append(index + 1).append(". ").append(tasks.get(index)).append('\n');
        }
        return result.toString().trim();
    }

    private String findTasks(String keyword) {
        require(!keyword.isEmpty(), "Provide a keyword to find matching tasks.");
        StringBuilder result = new StringBuilder("Matching tasks:\n");
        for (int index = 0; index < tasks.size(); index++) {
            if (tasks.get(index).hasKeyword(keyword)) {
                result.append(index + 1).append(". ").append(tasks.get(index)).append('\n');
            }
        }
        return result.length() == "Matching tasks:\n".length()
                ? "No tasks match that keyword." : result.toString().trim();
    }

    private String changeTask(String number, boolean completed) {
        Todo task = getTask(number);
        boolean changed = completed ? task.markComplete() : task.unmarkComplete();
        saveTasks();
        return changed ? (completed ? "✅ Marked as done:\n" : "↩ Marked as not done:\n") + task
                : completed ? "That task is already completed." : "That task is not completed yet.";
    }

    private String deleteTask(String number) {
        Todo task = tasks.remove(getIndex(number));
        saveTasks();
        return "Deleted:\n" + task;
    }

    private Todo getTask(String number) {
        return tasks.get(getIndex(number));
    }

    private int getIndex(String number) {
        try {
            int index = Integer.parseInt(number) - 1;
            require(index >= 0 && index < tasks.size(), "There is no task with that number.");
            return index;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Task number must be a number.");
        }
    }

    private void loadTasks() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            ArrayList<Todo> savedTasks = storage.loadFile(file);
            if (savedTasks != null) {
                tasks.addAll(savedTasks);
            }
        }
    }

    private void saveTasks() {
        try {
            storage.writeToFile(FILE_PATH, tasks);
        } catch (Exception exception) {
            // The user still sees their current session even if disk writing fails.
        }
    }

    private void addMessage(String text, boolean isUser) {
        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(580);
        bubble.getStyleClass().add(isUser ? "user-bubble" : "assistant-bubble");
        HBox row = new HBox(bubble);
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messages.getChildren().add(row);
        Platform.runLater(() -> messagePane.setVvalue(1));
    }

    private void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
