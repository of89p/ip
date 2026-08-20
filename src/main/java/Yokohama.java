import java.util.Locale;
import java.util.Scanner;

class Task {
    public String name;
    private boolean done;

    Task(String name) {
        this.name = name;
        this.done = false;
    }

    public boolean markComplete () {
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

    @Override
    public String toString() {
        return this.done ? String.format("[ X ] %s", this.name) : String.format("[  ] %s", this.name);
    }
}

public class Yokohama {
    public static void main(String[] args) {
        final String BOT_NAME = "Yokohama";
        Task[] todo_list = new Task[100];
        int pos = 0;

        String banner = "__   __  ___  _  __  ___  _   _    _    __  __    _    \n"
                + "\\ \\ / / / _ \\| |/ / / _ \\| | | |  / \\  |  \\/  |  / \\   \n"
                + " \\ V / | | | | ' / | | | | |_| | / _ \\ | |\\/| | / _ \\  \n"
                + "  | |  | |_| | . \\ | |_| |  _  |/ ___ \\| |  | |/ ___ \\ \n"
                + "  |_|   \\___/|_|\\_\\ \\___/|_| |_/_/   \\_\\_|  |_/_/   \\_\\\n";

        System.out.println("Hello, welcome to ");
        System.out.println(banner);

        System.out.println("Enter 'exit' to leave program. Yokohama would return all inputs as is.");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String lowerCaseInput = input.toLowerCase();

            if (lowerCaseInput.equals("exit")) {
                break;
            }

            if (lowerCaseInput.equals("list")) {
                for (int i = 0; i < pos; i++) {
                    System.out.printf("%d: %s\n", i+1, todo_list[i]);
                }
            } else if (lowerCaseInput.startsWith("mark")) {
                int todo_index = Integer.parseInt(lowerCaseInput.substring(4).replaceAll("\\s", "")) - 1;
                Task task = todo_list[todo_index];
                if (!task.markComplete()) {
                    System.out.println("Already marked as completed! Do you mean to unmark?");
                } else {
                    System.out.printf("Marked as done: \n   %s \n", task.toString());
                }
            } else if (lowerCaseInput.startsWith("unmark")) {
                int todo_index = Integer.parseInt(lowerCaseInput.substring(6).replaceAll("\\s", "")) - 1;
                Task task = todo_list[todo_index];
                if (!task.unmarkComplete()) {
                    System.out.println("Task is not done yet! Do you mean to mark?");
                } else {
                    System.out.printf("Unmarked done: \n   %s \n", task.toString());
                }
            }
            else {
                todo_list[pos] = new Task(input);
                pos++;

                System.out.printf("Added: %s\n", input);
            }

        }

        scanner.close();
        System.out.println("Bye! Hope to see you again!");

    }
}
