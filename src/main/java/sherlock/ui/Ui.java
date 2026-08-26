package sherlock.ui;

import java.util.Scanner;

import sherlock.task.Task;
import sherlock.task.TaskList;

/**
 * Handles all console input and output for Sherlock.
 */
public class Ui implements AutoCloseable {
    private static final String BANNER = "  ____  _               _            _    \n"
            + " / ___|| |__   ___ _ __| | ___   ___| | __\n"
            + " \\___ \\| '_ \\ / _ \\ '__| |/ _ \\ / __| |/ /\n"
            + "  ___) | | | |  __/ |  | | (_) | (__|   < \n"
            + " |____/|_| |_|\\___|_|  |_|\\___/ \\___|_|\\_\\\n";
    private final Scanner scanner;

    /**
     * Creates a text user interface that reads commands from the given scanner.
     *
     * @param scanner command input source
     */
    public Ui(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Reads one full command from the user.
     *
     * @return entered command text
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays Sherlock's greeting. */
    public void showWelcome() {
        System.out.println(BANNER);
        System.out.println("Hello! I'm Sherlock, your detective assistant.");
        System.out.println("What can I do for you?");
    }

    /** Displays Sherlock's farewell. */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /** Displays all tasks in their list order. */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            System.out.println((index + 1) + ". " + tasks.get(index));
        }
    }

    /** Displays a newly added task. */
    public void showAddedTask(Task task) {
        System.out.println("added: " + task);
    }

    /** Displays a task marked as completed. */
    public void showMarkedAsDone(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /** Displays a task marked as incomplete. */
    public void showMarkedAsNotDone(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /** Displays a deleted task and remaining task count. */
    public void showDeletedTask(Task task, int remainingTaskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + remainingTaskCount + " tasks in the list.");
    }

    /** Displays an error caused by a user command. */
    public void showError(String message) {
        System.out.println("☹ OOPS!!! " + message);
    }

    /** Displays an error that occurred while loading saved tasks. */
    public void showLoadingError(String message) {
        System.out.println("☹ OOPS!!! I could not load saved tasks: " + message);
    }

    /** Closes the scanner used by this user interface. */
    @Override
    public void close() {
        scanner.close();
    }
}
