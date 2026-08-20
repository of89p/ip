import java.util.Locale;
import java.util.Scanner;

public class Yokohama {
    public static void main(String[] args) {
        final String BOT_NAME = "Yokohama";
        String[] todo_list = new String[100];
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
            } else {
                todo_list[pos] = input;
                pos++;

                System.out.printf("Added: %s\n", input);
            }

        }

        scanner.close();
        System.out.println("Bye! Hope to see you again!");

    }
}
