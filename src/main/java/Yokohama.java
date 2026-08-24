import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

enum Commands {
    EXIT, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT
}

enum TaskType {
    T, D, E
}

abstract class Todo {
    public String name;
    protected boolean done;

    Todo(String name, boolean done) {
        this.name = name;
        this.done = done;
    }

    public boolean markComplete() {
        if (this.done) {
            return false;
        } else {
            done = true;
            return true;
        }
    }

    public boolean unmarkComplete() {
        if (!this.done) {
            return false;
        } else {
            this.done = false;
            return true;
        }
    }

    protected static String convertFromLocalDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM/dd/yyyy HHmm");
        try {
            return dateTime.format(formatter);
        } catch (Exception e) {
            throw e;
        }
    }

    protected static String formatForUser(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
        return dateTime.format(formatter);
    }

    abstract public String toDbString();

    @Override
    public String toString() {
        return this.done ? String.format("[ X ] %s", this.name) : String.format("[  ] %s", this.name);
    }
}

class Task extends Todo {
    public Task(String description, boolean done) {
        super(description, done);
    }

    @Override
    public String toDbString() {
        return String.format("T | %s | %s\n", this.done ? "1" : "0", this.name);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}

class Deadline extends Todo {
    protected LocalDateTime by;

    public Deadline(String description, boolean done, LocalDateTime by) {
        super(description, done);
        this.by = by;
    }

    @Override
    public String toDbString() {
        return String.format("D | %s | %s | %s \n", this.done ? "1" : "0", this.name, this.by.toString());
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatForUser(this.by) + ")";
    }
}

class YokohamaException extends Exception {
    public YokohamaException(String message) {
        super(message);
    }
}

class Event extends Todo {
    protected LocalDateTime by;
    protected LocalDateTime to;

    public Event(String description, boolean done, LocalDateTime by, LocalDateTime to) {
        super(description, done);
        this.by = by;
        this.to = to;
    }

    @Override
    public String toDbString() {
        return String.format("E | %s | %s | %s | %s\n", this.done ? "1" : "0", this.name, this.by.toString(), this.to.toString());
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (by: " + super.convertFromLocalDateTime(this.by) + ", to: " + super.convertFromLocalDateTime(this.to) + ")";
    }
}

public class Yokohama {
    private static boolean isValidIndex(int index, int currentSize) {
        if (index < 0 || index >= currentSize) {
            System.out.println("No such task!");
            return false;
        }
        return true;
    }

    private static void writeToFile(String filePath, ArrayList<Todo> data) throws IOException {
        FileWriter fw = new FileWriter(filePath);

        String textToAdd = "";

        for (int i = 0; i < data.size(); i++) {
            textToAdd += data.get(i).toDbString();
        }

        fw.write(textToAdd);
        fw.close();
    }

    private static ArrayList<Todo> loadFile(File f) throws IOException {
        try {
            ArrayList<Todo> returnArr = new ArrayList<>();

            Scanner s = new Scanner(f);
            while (s.hasNext()) {
                String[] dataLine = s.nextLine().split("\\|+");
                String type = dataLine[0].trim();
                String done = dataLine[1].trim();
                String task = dataLine[2].trim();

                TaskType taskType;

                try {
                    taskType = TaskType.valueOf(type);

                    switch (taskType) {
                        case T:
                            returnArr.add(new Task(task, done.equals("1")));
                            break;

                        case D:
                            String by = dataLine[3].trim();
                            returnArr.add(new Deadline(task, done.equals("1"), LocalDateTime.parse(by)));
                            break;

                        case E:
                            String from = dataLine[3].trim();
                            String to = dataLine[4].trim();
                            returnArr.add(new Event(task, done.equals("1"), LocalDateTime.parse(from), LocalDateTime.parse(to)));
                            break;
                    }
                } catch (IllegalArgumentException e) {
                    throw new YokohamaException("Database error");
                }
            }

            System.out.println("Successfully loaded");
            return returnArr;
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        return null;
    }

    private static LocalDateTime convertToLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy HHmm");
        try {
            return LocalDateTime.parse(dateTime, formatter);
        } catch (Exception e) {
            throw e;
        }
    }

    private static String formatToReadable (LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy HHmm");
        try {
            return dateTime.format(formatter);
        } catch (Exception e) {
            throw e;
        }
    }

    public static void main(String[] args) {
        final String BOT_NAME = "Yokohama";
        final String FILEPATH = "src/main/java/todo_data.txt";
        ArrayList<Todo> todo_list = new ArrayList<>();

        File f = new File(FILEPATH);

        if(f.exists()) {
            try {
                todo_list = loadFile(f);
            } catch (Exception e) {
                System.out.println("An error occured: " + e.getMessage());
            }
        }

        String banner = "__   __  ___  _  __  ___  _   _    _    __  __    _    \n"
                + "\\ \\ / / / _ \\| |/ / / _ \\| | | |  / \\  |  \\/  |  / \\   \n"
                + " \\ V / | | | | ' / | | | | |_| | / _ \\ | |\\/| | / _ \\  \n"
                + "  | |  | |_| | . \\ | |_| |  _  |/ ___ \\| |  | |/ ___ \\ \n"
                + "  |_|   \\___/|_|\\_\\ \\___/|_| |_/_/   \\_\\_|  |_/_/   \\_\\\n";

        System.out.println("Hello, welcome to ");
        System.out.println(banner);
        System.out.println("Enter 'exit' to leave program. Yokohama would return all inputs as is.");

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

                switch (command) {
                    case EXIT:
                        isRunning = false;
                        try{
                            writeToFile(FILEPATH, todo_list);
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
                            throw new YokohamaException("Task number not provided!");
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
                            throw new YokohamaException("Task number not provided!");
                        }
                        try {
                            int todo_index = Integer.parseInt(parts[1]) - 1;
                            if (isValidIndex(todo_index, todo_list.size())) {
                                Todo todo = todo_list.get(todo_index);
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
                            throw new YokohamaException("Todo cannot be empty!");
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
                                LocalDateTime byConverted = convertToLocalDateTime(by);

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
                                LocalDateTime fromConverted = convertToLocalDateTime(from);
                                LocalDateTime toConverted = convertToLocalDateTime(to);
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
                System.out.println("   |\\---/|    ");
                System.out.println("   | x_x |    ");
                System.out.println("    \\_^_/     ");
                System.out.println("   /  _  \\    ");
                System.out.println("  |  / \\  |   ");
                System.out.println("  / |   | \\   ");
                System.out.println(" \"\"'     '\"\"  ");
                System.out.println("OOPS!!! " + e.getMessage()+"\n");
            }
        }

        scanner.close();
        System.out.println("Bye! Hope to see you again!");
    }
}