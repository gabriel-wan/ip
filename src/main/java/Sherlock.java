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

        Task[] tasks = new Task[100];
        int numberOfTasks = 0;
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String command = scanner.nextLine();
                if (command.equals("bye")) {
                    System.out.println("Bye. Hope to see you again soon!");
                    break;
                } else if (command.equals("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < numberOfTasks; i++) {
                        System.out.println((i + 1) + ". " + tasks[i]);
                    }
                } else if (command.startsWith("mark ")) {
                    int taskNumber = Integer.parseInt(command.substring(5));
                    Task completedTask = tasks[taskNumber - 1];
                    completedTask.markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + completedTask);
                } else if (command.startsWith("unmark ")) {
                    int taskNumber = Integer.parseInt(command.substring(7));
                    Task incompleteTask = tasks[taskNumber - 1];
                    incompleteTask.markAsNotDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + incompleteTask);
                } else {
                    tasks[numberOfTasks] = new Task(command);
                    numberOfTasks++;
                    System.out.println("added: " + tasks[numberOfTasks - 1]);
                }
            }
        }
    }
}

/**
 * Represents one task and whether it has been completed.
 */
class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task
     */
    Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task in the text UI's list format.
     *
     * @return the completion status followed by the task description
     */
    @Override
    public String toString() {
        String statusIcon = isDone ? "X" : " ";
        return "[" + statusIcon + "] " + description;
    }
}
