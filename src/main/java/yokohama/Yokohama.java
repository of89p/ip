package yokohama;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import yokohama.exceptions.YokohamaException;
import yokohama.parse.Commands;
import yokohama.storage.Storage;
import yokohama.task.Deadline;
import yokohama.task.Event;
import yokohama.task.Task;
import yokohama.task.Todo;
import yokohama.ui.Graphics;
import yokohama.utils.DateTimeHandler;

/**
 * Starts and coordinates the Yokohama task-list application.
 */
public class Yokohama {
    private static final String FILE_PATH = "data/todo_data.txt";
    private static final Graphics GRAPHICS = new Graphics();
    private static final Storage STORAGE = new Storage();

    private static boolean isValidIndex(int index, int currentSize) {
        if (index < 0 || index >= currentSize) {
            System.out.println("No such task!");
            return false;
        }
        return true;
    }

    /**
     * Starts the application and processes user commands until the user exits.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        ArrayList<Todo> todoList = new ArrayList<>();

        File file = new File(FILE_PATH);

        if (file.exists()) {
            try {
                todoList = STORAGE.loadFile(file);
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }

        GRAPHICS.printWelcomeBanner();

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            try {
                String[] parts = input.split("\\s+");
                Commands command;

                try {
                    command = Commands.valueOf(parts[0].toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new YokohamaException("Unrecognized command!");
                }

                switch (command) {
                    case EXIT:
                        isRunning = false;
                        try {
                            STORAGE.writeToFile(FILE_PATH, todoList);
                            System.out.println("Successfully saved data.");
                        } catch (Exception e) {
                            throw new YokohamaException("Writing to file failed! Error: " + e.getMessage());
                        }
                        break;

                    case LIST:
                        if (todoList.isEmpty()) {
                            System.out.println("   /\\_/\\    ");
                            System.out.println("  ( ^_^ )   ");
                            System.out.println("  /  _  \\   ");
                            System.out.println(" (__(__)_)  ");
                            System.out.println("Yay! Your list is totally empty. Time to relax!");
                        } else {
                            for (int i = 0; i < todoList.size(); i++) {
                                System.out.printf("%d: %s\n", i + 1, todoList.get(i));
                            }
                        }
                        break;

                    case FIND:
                        String keyword = input.substring(parts[0].length()).trim();
                        if (keyword.isEmpty()) {
                            throw new YokohamaException("Provide a keyword to find matching tasks!");
                        }

                        boolean hasMatchingTask = false;
                        System.out.println("Here are the matching tasks in your list:");
                        for (int i = 0; i < todoList.size(); i++) {
                            Todo task = todoList.get(i);
                            if (task.hasKeyword(keyword)) {
                                System.out.printf("%d: %s\n", i + 1, task);
                                hasMatchingTask = true;
                            }
                        }

                        if (!hasMatchingTask) {
                            System.out.println("No tasks match that keyword.");
                        }
                        break;

                    case MARK:
                        if (parts.length < 2) {
                            throw new YokohamaException("Task number not provided!");
                        }
                        try {
                            int taskIndex = Integer.parseInt(parts[1]) - 1;
                            if (isValidIndex(taskIndex, todoList.size())) {
                                Todo todo = todoList.get(taskIndex);
                                if (!todo.markComplete()) {
                                    System.out.println("Already marked as completed! Do you mean to unmark?");
                                } else {
                                    System.out.printf("Marked as done: \n   %s \n", todo.toString());
                                }
                            }
                        } catch (NumberFormatException e) {
                            throw new YokohamaException("Enter a number!");
                        }
                        break;

                    case UNMARK:
                        if (parts.length < 2) {
                            throw new YokohamaException("Task number not provided!");
                        }
                        try {
                            int taskIndex = Integer.parseInt(parts[1]) - 1;
                            if (isValidIndex(taskIndex, todoList.size())) {
                                Todo todo = todoList.get(taskIndex);
                                if (!todo.unmarkComplete()) {
                                    System.out.println("Task is not done yet! Do you mean to mark?");
                                } else {
                                    System.out.printf("Unmarked done: \n   %s \n", todo.toString());
                                }
                            }
                        } catch (NumberFormatException e) {
                            throw new YokohamaException("Enter a number!");
                        }
                        break;

                    case DELETE:
                        if (parts.length < 2) {
                            throw new YokohamaException("Task number not provided!");
                        }
                        try {
                            int taskIndex = Integer.parseInt(parts[1]) - 1;
                            if (isValidIndex(taskIndex, todoList.size())) {
                                String deletedTask = todoList.get(taskIndex).toString();
                                todoList.remove(taskIndex);
                                System.out.printf("Deleted: \n%s\n", deletedTask);
                                System.out.printf("There are now %d items in todo-list. \n", todoList.size());
                            }
                        } catch (NumberFormatException e) {
                            throw new YokohamaException("Enter a number!");
                        }
                        break;

                    case TODO:
                        String todoDescription = input.substring(parts[0].length()).trim();
                        if (todoDescription.isEmpty()) {
                            throw new YokohamaException("Todo cannot be empty!");
                        }
                        todoList.add(new Task(todoDescription, false));
                        System.out.printf("Added: %s\n", input);
                        System.out.printf("There are now %d items in todo-list. \n", todoList.size());
                        break;

                    case DEADLINE:
                        String deadlinePayload = input.substring(parts[0].length()).trim();
                        String[] deadlineParts = deadlinePayload.split(" /by ");
                        if (deadlineParts.length < 2) {
                            throw new YokohamaException("Provide a deadline using '/by'");
                        } else {
                            String description = deadlineParts[0].trim();
                            String by = deadlineParts[1].trim();

                            try {
                                LocalDateTime byConverted = DateTimeHandler.convertToLocalDateTime(by);

                                todoList.add(new Deadline(description, false, byConverted));
                                System.out.printf("Added: \n%s\n", todoList.getLast().toString());
                                System.out.printf("There are now %d items in todo-list. \n", todoList.size());
                            } catch (Exception e) {
                                throw new YokohamaException(e.getMessage());
                            }
                        }
                        break;

                    case EVENT:
                        String eventPayload = input.substring(parts[0].length()).trim();
                        int fromIndex = eventPayload.indexOf(" /from ");
                        int toIndex = eventPayload.indexOf(" /to ");

                        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
                            throw new YokohamaException(
                                    "Please provide an event using '/from' and '/to' in the correct order.");
                        } else {
                            String description = eventPayload.substring(0, fromIndex).trim();
                            String from = eventPayload.substring(fromIndex + 7, toIndex).trim();
                            String to = eventPayload.substring(toIndex + 5).trim();

                            try {
                                LocalDateTime fromConverted = DateTimeHandler.convertToLocalDateTime(from);
                                LocalDateTime toConverted = DateTimeHandler.convertToLocalDateTime(to);
                                todoList.add(new Event(description, false, fromConverted, toConverted));

                                System.out.printf("Added: \n%s\n", todoList.getLast().toString());
                                System.out.printf("There are now %d items in todo-list. \n", todoList.size());
                            } catch (Exception e) {
                                throw new YokohamaException(e.getMessage());
                            }
                        }
                        break;

                    default:
                        break;
                }

            } catch (YokohamaException e) {
                GRAPHICS.printErrorCat(e);
            }
        }

        scanner.close();
        System.out.println("Bye! Hope to see you again!");
    }
}
