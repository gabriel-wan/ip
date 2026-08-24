import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The entry point for Sherlock, a detective-themed personal assistant chatbot.
 */
public class Sherlock {
    private static final Path SAVE_FILE = Path.of("data", "sherlock.txt");

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

        Storage storage = new Storage(SAVE_FILE);
        TaskList tasks = storage.load();
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
                        storage.save(tasks);
                        System.out.println("Nice! I've marked this task as done:");
                        System.out.println("  " + completedTask);
                    } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                        int taskNumber = parseTaskNumber(command.substring(6), tasks.size());
                        Task incompleteTask = tasks.get(taskNumber - 1);
                        incompleteTask.markAsNotDone();
                        storage.save(tasks);
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.println("  " + incompleteTask);
                    } else if (command.equals("delete") || command.startsWith("delete ")) {
                        int taskNumber = parseTaskNumber(command.substring(6), tasks.size());
                        Task deletedTask = tasks.delete(taskNumber - 1);
                        storage.save(tasks);
                        System.out.println("Noted. I've removed this task:");
                        System.out.println("  " + deletedTask);
                        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                    } else if (command.equals("todo") || command.startsWith("todo ")) {
                        String description = requireText(command.substring(4), "I need a case description before I can add it.");
                        tasks.add(new Todo(description));
                        storage.save(tasks);
                        System.out.println("added: " + tasks.get(tasks.size() - 1));
                    } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                        String[] details = command.substring(8).trim().split(" /by ", 2);
                        if (details.length != 2) {
                            throw new SherlockException("A deadline must include /by followed by a time.");
                        }
                        String description = requireText(details[0], "The description of a deadline cannot be empty.");
                        String by = requireText(details[1], "The time of a deadline cannot be empty.");
                        tasks.add(new Deadline(description, by));
                        storage.save(tasks);
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
                        storage.save(tasks);
                        System.out.println("added: " + tasks.get(tasks.size() - 1));
                    } else {
                        throw new SherlockException("That command is not in my casebook. Try another clue.");
                    }
                } catch (SherlockException | IOException exception) {
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
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list with an initial capacity.
     *
     * @param capacity initial number of tasks the list can hold without resizing
     */
    TaskList(int capacity) {
        tasks = new ArrayList<>(capacity);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task task to add
     */
    void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return the task at the given index
     */
    Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return the removed task
     */
    Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks currently in this list.
     *
     * @return current task count
     */
    int size() {
        return tasks.size();
    }
}

/**
 * Saves tasks to, and restores tasks from, Sherlock's local data file.
 */
class Storage {
    private final Path filePath;

    /**
     * Creates storage backed by the given relative data-file path.
     *
     * @param filePath location of Sherlock's saved tasks
     */
    Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads saved tasks, creating the data folder and empty file on first use.
     *
     * @return the restored task list
     */
    TaskList load() {
        TaskList tasks = new TaskList(100);
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                return tasks;
            }
            for (String line : Files.readAllLines(filePath)) {
                if (!line.isBlank()) {
                    tasks.add(parseTask(line));
                }
            }
        } catch (IOException | SherlockException exception) {
            System.out.println("☹ OOPS!!! I could not load saved tasks: " + exception.getMessage());
        }
        return tasks;
    }

    /**
     * Replaces the saved data with the current list of tasks.
     *
     * @param tasks task list to persist
     * @throws IOException if the file cannot be written
     */
    void save(TaskList tasks) throws IOException {
        List<String> lines = new ArrayList<>();
        for (int index = 0; index < tasks.size(); index++) {
            lines.add(tasks.get(index).toFileString());
        }
        Files.write(filePath, lines);
    }

    /**
     * Converts one saved data-file line into a task.
     *
     * @param line task record from the data file
     * @return reconstructed task
     * @throws SherlockException if the record is invalid
     */
    private Task parseTask(String line) throws SherlockException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3) {
            throw new SherlockException("a saved task has an invalid format.");
        }
        Task task;
        switch (fields[0]) {
        case "T":
            task = new Todo(fields[2]);
            break;
        case "D":
            if (fields.length != 4) {
                throw new SherlockException("a saved deadline has an invalid format.");
            }
            task = new Deadline(fields[2], fields[3]);
            break;
        case "E":
            if (fields.length != 5) {
                throw new SherlockException("a saved event has an invalid format.");
            }
            task = new Event(fields[2], fields[3], fields[4]);
            break;
        default:
            throw new SherlockException("a saved task has an unknown type.");
        }
        if (fields[1].equals("1")) {
            task.markAsDone();
        } else if (!fields[1].equals("0")) {
            throw new SherlockException("a saved task has an invalid completion state.");
        }
        return task;
    }
}

/**
 * Represents one task and whether it has been completed.
 */
abstract class Task {
    private final String description;
    private TaskStatus status;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task
     */
    Task(String description) {
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Marks this task as completed.
     */
    void markAsDone() {
        status = TaskStatus.DONE;
    }

    /**
     * Marks this task as incomplete.
     */
    void markAsNotDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the letter that identifies this task type in the text UI.
     *
     * @return the task type icon
     */
    abstract String getTypeIcon();

    /**
     * Returns this task in the compact format used in Sherlock's data file.
     *
     * @return persistent representation of this task
     */
    String toFileString() {
        return getTypeIcon() + " | " + (status == TaskStatus.DONE ? "1" : "0") + " | " + description;
    }

    /**
     * Returns the common task details in the text UI's list format.
     *
     * @return the type and completion status followed by the task description
     */
    @Override
    public String toString() {
        String statusIcon = status == TaskStatus.DONE ? "X" : " ";
        return "[" + getTypeIcon() + "][" + statusIcon + "] " + description;
    }
}

/**
 * Represents the only valid completion states of a task.
 */
enum TaskStatus {
    NOT_DONE,
    DONE
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
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");
    private final LocalDate by;

    Deadline(String description, String by) {
        super(description);
        this.by = LocalDate.parse(by);
    }

    @Override
    String getTypeIcon() {
        return "D";
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }

    @Override
    String toFileString() {
        return super.toFileString() + " | " + by;
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

    @Override
    String toFileString() {
        return super.toFileString() + " | " + from + " | " + to;
    }
}
