import java.util.Scanner;

/**
 * The entry point for Sherlock, a detective-themed personal assistant chatbot.
 */
public class Sherlock {
    /**
     * Starts Sherlock, stores entered tasks, lists them on request, and exits on {@code bye}.
     *
     * @param args command-line arguments, which are not used at this level
     */
    public static void main(String[] args) {
        String banner = "  ____  _               _            _    \n"
                + " / ___|| |__   ___ _ __| | ___   ___| | __\n"
                + " \\___ \\| '_ \\ / _ \\ '__| |/ _ \\ / __| |/ /\n"
                + "  ___) | | | |  __/ |  | | (_) | (__|   < \n"
                + " |____/|_| |_|\\___|_|  |_|\\___/ \\___|_|\\_\\\n";

        System.out.println(banner);
        System.out.println("Hello! I'm Sherlock, your detective assistant.");
        System.out.println("What can I do for you?");

        String[] tasks = new String[100];
        int numberOfTasks = 0;
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String command = scanner.nextLine();
                if (command.equals("bye")) {
                    System.out.println("Bye. Hope to see you again soon!");
                    break;
                } else if (command.equals("list")) {
                    for (int i = 0; i < numberOfTasks; i++) {
                        System.out.println((i + 1) + ". " + tasks[i]);
                    }
                } else {
                    tasks[numberOfTasks] = command;
                    numberOfTasks++;
                    System.out.println("added: " + command);
                }
            }
        }
    }
}
