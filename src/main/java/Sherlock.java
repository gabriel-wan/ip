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
                } else if (command.startsWith("todo ")) {
                    tasks[numberOfTasks] = new Todo(command.substring(5));
                    numberOfTasks++;
                    System.out.println("added: " + tasks[numberOfTasks - 1]);
                } else if (command.startsWith("deadline ")) {
                    String[] details = command.substring(9).split(" /by ", 2);
                    tasks[numberOfTasks] = new Deadline(details[0], details[1]);
                    numberOfTasks++;
                    System.out.println("added: " + tasks[numberOfTasks - 1]);
                } else if (command.startsWith("event ")) {
                    String[] details = command.substring(6).split(" /from | /to ", 3);
                    tasks[numberOfTasks] = new Event(details[0], details[1], details[2]);
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
abstract class Task {
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
     * Returns the letter that identifies this task type in the text UI.
     *
     * @return the task type icon
     */
    abstract String getTypeIcon();

    /**
     * Returns the common task details in the text UI's list format.
     *
     * @return the type and completion status followed by the task description
     */
    @Override
    public String toString() {
        String statusIcon = isDone ? "X" : " ";
        return "[" + getTypeIcon() + "][" + statusIcon + "] " + description;
    }
}

/**
 * Represents a task without a date or time.
 */
class Todo extends Task {
    Todo(String description) {
        super(description);
    }

    @Override
    String getTypeIcon() {
        return "T";
    }
}

/**
 * Represents a task that must be completed by a specified time.
 */
class Deadline extends Task {
    private final String by;

    Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    String getTypeIcon() {
        return "D";
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}

/**
 * Represents an event that occurs during a specified time period.
 */
class Event extends Task {
    private final String from;
    private final String to;

    Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    String getTypeIcon() {
        return "E";
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
