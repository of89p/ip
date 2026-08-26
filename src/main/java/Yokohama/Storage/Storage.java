package Yokohama.Storage;

import Yokohama.exceptions.YokohamaException;
import Yokohama.task.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles the reading and writing of data to data file.
 */
public class Storage {
    /**
     * Writes data to filePath.
     * Mainly to save data of one session when user exits program.
     *
     * @param filePath Filepath of file to write to.
     * @param data The data written to file.
     * @throws IOException When there is an error opening/creating the file to be written to.
     */
    public void writeToFile(String filePath, ArrayList<Todo> data) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileWriter fw = new FileWriter(file);

        String textToAdd = "";

//        TODO: Change to stringbuilder
        for (int i = 0; i < data.size(); i++) {
            textToAdd += data.get(i).toDbString();
        }

        fw.write(textToAdd);
        fw.close();
    }

    /**
     * Reads data from storage file.
     * Mainly to read data when the program starts.
     *
     * @param f File object that contains data from the previous session.
     * @return Returns a new ArrayList that represents data from the data file.
     * @throws IOException When there is an error opening/creating the file to be written to.
     */
    public ArrayList<Todo> loadFile(File f) throws IOException {
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
}
