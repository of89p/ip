package yokohama.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import yokohama.exceptions.YokohamaException;
import yokohama.task.Deadline;
import yokohama.task.Event;
import yokohama.task.Task;
import yokohama.task.TaskType;
import yokohama.task.Todo;

/**
 * Reads tasks from and writes tasks to the application's data file.
 */
public class Storage {
    /**
     * Writes the specified tasks to the data file at the given path.
     *
     * @param filePath Path of the data file.
     * @param tasks Tasks to write.
     * @throws IOException If the file cannot be created or written.
     */
    public void writeToFile(String filePath, List<Todo> tasks) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        StringBuilder textToAdd = new StringBuilder();
        for (Todo task : tasks) {
            textToAdd.append(task.toDbString());
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(textToAdd.toString());
        }
    }

    /**
     * Loads tasks from the specified data file.
     *
     * @param file Data file to load.
     * @return Tasks represented in the data file, or {@code null} if loading fails.
     */
    public ArrayList<Todo> loadFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            ArrayList<Todo> tasks = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String[] dataLine = scanner.nextLine().split("\\|+");
                String type = dataLine[0].trim();
                String done = dataLine[1].trim();
                String description = dataLine[2].trim();

                TaskType taskType;

                try {
                    taskType = TaskType.valueOf(type);

                    switch (taskType) {
                        case T -> tasks.add(new Task(description, done.equals("1")));
                        case D -> {
                            String deadline = dataLine[3].trim();
                            tasks.add(new Deadline(description, done.equals("1"),
                                    LocalDateTime.parse(deadline)));
                        }
                        case E -> {
                            String start = dataLine[3].trim();
                            String end = dataLine[4].trim();
                            tasks.add(new Event(description, done.equals("1"), LocalDateTime.parse(start),
                                    LocalDateTime.parse(end)));
                        }
                    }
                } catch (IllegalArgumentException e) {
                    throw new YokohamaException("Database error");
                }
            }

            System.out.println("Successfully loaded");
            return tasks;
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        return null;
    }
}
