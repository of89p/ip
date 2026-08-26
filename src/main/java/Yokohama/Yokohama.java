package Yokohama;

import Yokohama.Storage.Storage;
import Yokohama.exceptions.YokohamaException;
import Yokohama.Ui.Graphics;
import Yokohama.parse.Commands;
import Yokohama.task.*;
import Yokohama.utils.DateTimeHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;

public class Yokohama {
    static Graphics graphics = new Graphics();
    static Storage storage = new Storage();

    private static boolean isValidIndex(int index, int currentSize) {
        if (index < 0 || index >= currentSize) {
            System.out.println("No such task!");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        final String BOT_NAME = "Yokohama";
        final String FILEPATH = "data/todo_data.txt";
        ArrayList<Todo> todo_list = new ArrayList<>();

        File f = new File(FILEPATH);

        if(f.exists()) {
            try {
                todo_list = storage.loadFile(f);
            } catch (Exception e) {
                System.out.println("An error occured: " + e.getMessage());
            }
        }

        graphics.printWelcomeBanner();

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
                    throw new YokohamaException("Unrecognised command!");
                }

                DateTimeHandler dateTimeHandler = new DateTimeHandler();

                switch (command) {
                    case EXIT:
                        isRunning = false;
                        try{
                            storage.writeToFile(FILEPATH, todo_list);
                            System.out.println("Successfully saved data.");
                        } catch (Exception e) {
                            throw new YokohamaException("Writing to file failed! Error: " + e.getMessage());
                        }
                        break;

                    case LIST:
                        if (todo_list.isEmpty()) {
                            System.out.println("   /\\_/\\    ");
                            System.out.println("  ( ^_^ )   ");
                            System.out.println("  /  _  \\   ");
                            System.out.println(" (__(__)_)  ");
                            System.out.println("Yay! Your list is totally empty. Time to relax!");
                        } else {
                            for (int i = 0; i < todo_list.size(); i++) {
                                System.out.printf("%d: %s\n", i + 1, todo_list.get(i));
                            }
                        }
                        break;

                    case MARK:
                        if (parts.length < 2) {
                            throw new YokohamaException("Yokohama.task.Task number not provided!");
                        }
                        try {
                            int todo_index = Integer.parseInt(parts[1]) - 1;
                            if (isValidIndex(todo_index, todo_list.size())) {
                                Todo todo = todo_list.get(todo_index);
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
                            throw new YokohamaException("Yokohama.task.Task number not provided!");
                        }
                        try {
                            int todo_index = Integer.parseInt(parts[1]) - 1;
                            if (isValidIndex(todo_index, todo_list.size())) {
                                Todo todo = todo_list.get(todo_index);
                                if (!todo.unmarkComplete()) {
                                    System.out.println("Yokohama.task.Task is not done yet! Do you mean to mark?");
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
                            throw new YokohamaException("Yokohama.task.Task number not provided!");
                        }
                        try {
                            int delete_index = Integer.parseInt(parts[1]) - 1;
                            if (isValidIndex(delete_index, todo_list.size())) {
                                String deletedTask = todo_list.get(delete_index).toString();
                                todo_list.remove(delete_index);
                                System.out.printf("Deleted: \n%s\n", deletedTask);
                                System.out.printf("There are now %d items in todo-list. \n", todo_list.size());
                            }
                        } catch (NumberFormatException e) {
                            throw new YokohamaException("Enter a number!");
                        }
                        break;

                    case TODO:
                        String todoDescription = input.substring(parts[0].length()).trim();
                        if (todoDescription.isEmpty()) {
                            throw new YokohamaException("Yokohama.task.Todo cannot be empty!");
                        }
                        todo_list.add(new Task(todoDescription, false));
                        System.out.printf("Added: %s\n", input);
                        System.out.printf("There are now %d items in todo-list. \n", todo_list.size());
                        break;

                    case DEADLINE:
                        String deadlinePayload = input.substring(parts[0].length()).trim();
                        String[] deadline_parts = deadlinePayload.split(" /by ");
                        if (deadline_parts.length < 2) {
                            throw new YokohamaException("Provide a deadline using '/by'");
                        } else {
                            String description = deadline_parts[0].trim();
                            String by = deadline_parts[1].trim();

                            try {
                                LocalDateTime byConverted = dateTimeHandler.convertToLocalDateTime(by);

                                todo_list.add(new Deadline(description, false, byConverted));
                                System.out.printf("Added: \n%s\n", todo_list.getLast().toString());
                                System.out.printf("There are now %d items in todo-list. \n", todo_list.size());
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
                            throw new YokohamaException("Please provide an event using '/from' and '/to' in the correct order.");
                        } else {
                            String description = eventPayload.substring(0, fromIndex).trim();
                            String from = eventPayload.substring(fromIndex + 7, toIndex).trim();
                            String to = eventPayload.substring(toIndex + 5).trim();

                            try {
                                LocalDateTime fromConverted = dateTimeHandler.convertToLocalDateTime(from);
                                LocalDateTime toConverted = dateTimeHandler.convertToLocalDateTime(to);
                                todo_list.add(new Event(description, false, fromConverted, toConverted));

                                System.out.printf("Added: \n%s\n", todo_list.getLast().toString());
                                System.out.printf("There are now %d items in todo-list. \n", todo_list.size());
                            } catch (Exception e) {
                                throw new YokohamaException(e.getMessage());
                            }
                        }
                        break;
                }

            } catch (YokohamaException e) {
                graphics.printErrorCat(e);
            }
        }

        scanner.close();
        System.out.println("Bye! Hope to see you again!");
    }
}