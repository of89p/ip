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

class Deadline extends Task {
    protected String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by + ")";
    }
}


class Event extends Task {
    protected String by;
    protected String to;

    public Event(String description, String by, String to) {
        super(description);
        this.by = by;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by + ", to: " + this.to + ")";
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
            String input = scanner.nextLine().trim();
            String lowerCaseInput = input.toLowerCase();

            if(input.isEmpty()) {
                continue;
            }

            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();

            if (command.equals("exit")) {
                break;
            }

            if (command.equals("list")) {
                for (int i = 0; i < pos; i++) {
                    System.out.printf("%d: %s\n", i+1, todo_list[i]);
                }
            } else if (command.equals("mark")) {
                if(parts.length < 2) {
                    System.out.println("Task number not provided");
                    continue;
                }

                try {
                    int todo_index = Integer.parseInt(parts[1]) - 1;

                    if(isValidIndex(todo_index, pos)) {
                        Task task = todo_list[todo_index];
                        if (!task.markComplete()) {
                            System.out.println("Already marked as completed! Do you mean to unmark?");
                        } else {
                            System.out.printf("Marked as done: \n   %s \n", task.toString());
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Not a number. Please use digits.");
                }

            } else if (command.equals("unmark")) {
                if(parts.length < 2) {
                    System.out.println("Task number not provided");
                    continue;
                }

                try {
                    int todo_index = Integer.parseInt(parts[1]) - 1;

                    if(isValidIndex(todo_index, pos)) {
                        Task task = todo_list[todo_index];
                        if (!task.unmarkComplete()) {
                            System.out.println("Task is not done yet! Do you mean to mark?");
                        } else {
                            System.out.printf("Unmarked done: \n   %s \n", task.toString());
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Not a number. Please use digits.");
                }
            } else if (command.equals("todo")) {
                String description = input.substring(command.length()).trim();
                todo_list[pos] = new Task(description);
                pos++;

                System.out.printf("Added: %s\n", input);
                System.out.printf("There are now %d items in todo-list. \n", pos);
            } else if (command.equals("deadline")) {
                String payload = input.substring(8).trim();

                String[] deadline_parts = payload.split(" /by ");

                if (deadline_parts.length < 2) {
                    System.out.println("Provide a deadline using '/by'.");
                } else {
                    String description = deadline_parts[0].trim();
                    String by = deadline_parts[1].trim();

                    todo_list[pos] = new Deadline(description, by);
                    pos++;

                    System.out.printf("Added: \n%s\n", todo_list[pos-1].toString());
                    System.out.printf("There are now %d items in todo-list. \n", pos);
                }
            } else if (command.equals("event")) {
                String payload = input.substring(5).trim();

                int fromIndex = payload.indexOf(" /from ");
                int toIndex = payload.indexOf(" /to ");

                if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
                    System.out.println("Please provide an event using '/from' and '/to' in the correct order.");
                } else {
                    String description = payload.substring(0, fromIndex).trim();
                    String from = payload.substring(fromIndex + 7, toIndex).trim();
                    String to = payload.substring(toIndex + 5).trim();

                    todo_list[pos] = new Event(description, from, to);
                    pos++;

                    System.out.printf("Added: \n%s\n", todo_list[pos-1].toString());
                    System.out.printf("There are now %d items in todo-list. \n", pos);

                }
            }

        }

        scanner.close();
        System.out.println("Bye! Hope to see you again!");

    }
}
