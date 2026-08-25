package Yokohama.Ui;

public class Graphics {
    public void printWelcomeBanner () {
        String banner = "__   __  ___  _  __  ___  _   _    _    __  __    _    \n"
                + "\\ \\ / / / _ \\| |/ / / _ \\| | | |  / \\  |  \\/  |  / \\   \n"
                + " \\ V / | | | | ' / | | | | |_| | / _ \\ | |\\/| | / _ \\  \n"
                + "  | |  | |_| | . \\ | |_| |  _  |/ ___ \\| |  | |/ ___ \\ \n"
                + "  |_|   \\___/|_|\\_\\ \\___/|_| |_/_/   \\_\\_|  |_/_/   \\_\\\n";

        System.out.println("Hello, welcome to ");
        System.out.println(banner);
        System.out.println("Enter 'exit' to leave program. Yokohama.Yokohama would return all inputs as is.");
    }

    public void printErrorCat(Exception e) {
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
