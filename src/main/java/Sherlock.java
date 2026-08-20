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

        TaskList tasks = new TaskList(100);
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String command = scanner.nextLine();
                try {
                    if (command.equals("bye")) {
                        System.out.println("Bye. Hope to see you again soon!");
                        break;
                    } else if (command.equals("list")) {
                        System.out.println("Here are the tasks in your list:");
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.println((i + 1) + ". " + tasks.get(i));
                        }
                    } else if (command.equals("mark") || command.startsWith("mark ")) {
                        int taskNumber = parseTaskNumber(command.substring(4), tasks.size());
                        Task completedTask = tasks.get(taskNumber - 1);
                        completedTask.markAsDone();
                        System.out.println("Nice! I've marked this task as done:");
                        System.out.println("  " + completedTask);
                    } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                        int taskNumber = parseTaskNumber(command.substring(6), tasks.size());
                        Task incompleteTask = tasks.get(taskNumber - 1);
                        incompleteTask.markAsNotDone();
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.println("  " + incompleteTask);
                    } else if (command.equals("delete") || command.startsWith("delete ")) {
                        int taskNumber = parseTaskNumber(command.substring(6), tasks.size());
                        Task deletedTask = tasks.delete(taskNumber - 1);
                        System.out.println("Noted. I've removed this task:");
                        System.out.println("  " + deletedTask);
                        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                    } else if (command.equals("todo") || command.startsWith("todo ")) {
                        String description = requireText(command.substring(4), "I need a case description before I can add it.");
                        tasks.add(new Todo(description));
                        System.out.println("added: " + tasks.get(tasks.size() - 1));
                    } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                        String[] details = command.substring(8).trim().split(" /by ", 2);
                        if (details.length != 2) {
                            throw new SherlockException("A deadline must include /by followed by a time.");
                        }
                        String description = requireText(details[0], "The description of a deadline cannot be empty.");
                        String by = requireText(details[1], "The time of a deadline cannot be empty.");
                        tasks.add(new Deadline(description, by));
                        System.out.println("added: " + tasks.get(tasks.size() - 1));
                    } else if (command.equals("event") || command.startsWith("event ")) {
                        String[] details = command.substring(5).trim().split(" /from | /to ", 3);
                        if (details.length != 3) {
                            throw new SherlockException("An event must include /from and /to times.");
                        }
                        String description = requireText(details[0], "The description of an event cannot be empty.");
                        String from = requireText(details[1], "The start time of an event cannot be empty.");
                        String to = requireText(details[2], "The end time of an event cannot be empty.");
                        tasks.add(new Event(description, from, to));
                        System.out.println("added: " + tasks.get(tasks.size() - 1));
                    } else {
                        throw new SherlockException("That command is not in my casebook. Try another clue.");
                    }
                } catch (SherlockException exception) {
                    System.out.println("☹ OOPS!!! " + exception.getMessage());
                }
            }
        }
    }

    /**
     * Validates and converts a one-based task number supplied in a command.
     *
     * @param input task number text
     * @param taskCount number of tasks currently stored
     * @return the validated task number
     * @throws SherlockException if the input is not a valid task number
     */
    private static int parseTaskNumber(String input, int taskCount) throws SherlockException {
        try {
            int taskNumber = Integer.parseInt(input.trim());
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new SherlockException("The task number must refer to a task in the list.");
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw new SherlockException("The task number must be a whole number.");
        }
    }

    /**
     * Ensures that a required command component contains non-whitespace text.
     *
     * @param text command component to validate
     * @param errorMessage message to show when the component is absent
     * @return the trimmed component
     * @throws SherlockException if the component is empty
     */
    private static String requireText(String text, String errorMessage) throws SherlockException {
        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            throw new SherlockException(errorMessage);
        }
        return trimmedText;
    }

}

/**
 * Represents an error caused by an invalid Sherlock command or command argument.
 */
class SherlockException extends Exception {
    SherlockException(String message) {
        super(message);
    }
}

/**
 * Stores Sherlock's tasks and provides indexed access to them.
 */
class TaskList {
    private final Task[] tasks;
    private int size;

    /**
     * Creates an empty task list with a fixed maximum capacity.
     *
     * @param capacity maximum number of tasks the list can hold
     */
    TaskList(int capacity) {
        tasks = new Task[capacity];
        size = 0;
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task task to add
     * @throws SherlockException if the list has reached its capacity
     */
    void add(Task task) throws SherlockException {
        if (size == tasks.length) {
            throw new SherlockException("The task list is full.");
        }
        tasks[size] = task;
        size++;
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return the task at the given index
     */
    Task get(int index) {
        return tasks[index];
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return the removed task
     */
    Task delete(int index) {
        Task deletedTask = tasks[index];
        for (int i = index; i < size - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[size - 1] = null;
        size--;
        return deletedTask;
    }

    /**
     * Returns the number of tasks currently in this list.
     *
     * @return current task count
     */
    int size() {
        return size;
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
