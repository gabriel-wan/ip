import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * The entry point for Sherlock, a detective-themed personal assistant chatbot.
 */
public class Sherlock {
    private static final Path SAVE_FILE = Path.of("data", "sherlock.txt");
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    /**
     * Creates the application and restores saved tasks where possible.
     *
     * @param filePath location of the task data file
     * @param ui user interface used for all interaction
     */
    Sherlock(Path filePath, Ui ui) {
        this.storage = new Storage(filePath);
        this.ui = ui;
    }

    /**
     * Starts Sherlock, stores entered tasks, lists them on request, and exits on {@code bye}.
     *
     * @param args command-line arguments, which are not used at this level
     */
    public static void main(String[] args) {
        try (Ui ui = new Ui(new Scanner(System.in))) {
            new Sherlock(SAVE_FILE, ui).run();
        }
    }

    /**
     * Runs Sherlock's command loop until the user exits.
     */
    void run() {
        ui.showWelcome();
        tasks = loadTasks();
        while (true) {
            String command = ui.readCommand();
            try {
                if (command.equals("bye")) {
                    ui.showGoodbye();
                    return;
                } else if (command.equals("list")) {
                    ui.showTaskList(tasks);
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    int taskNumber = parseTaskNumber(command.substring(4), tasks.size());
                    Task completedTask = tasks.get(taskNumber - 1);
                    completedTask.markAsDone();
                    storage.save(tasks);
                    ui.showMarkedAsDone(completedTask);
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    int taskNumber = parseTaskNumber(command.substring(6), tasks.size());
                    Task incompleteTask = tasks.get(taskNumber - 1);
                    incompleteTask.markAsNotDone();
                    storage.save(tasks);
                    ui.showMarkedAsNotDone(incompleteTask);
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    int taskNumber = parseTaskNumber(command.substring(6), tasks.size());
                    Task deletedTask = tasks.delete(taskNumber - 1);
                    storage.save(tasks);
                    ui.showDeletedTask(deletedTask, tasks.size());
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = requireText(command.substring(4), "I need a case description before I can add it.");
                    tasks.add(new Todo(description));
                    storage.save(tasks);
                    ui.showAddedTask(tasks.get(tasks.size() - 1));
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    String[] details = command.substring(8).trim().split(" /by ", 2);
                    if (details.length != 2) {
                        throw new SherlockException("A deadline must include /by followed by a time.");
                    }
                    String description = requireText(details[0], "The description of a deadline cannot be empty.");
                    String by = requireText(details[1], "The time of a deadline cannot be empty.");
                    tasks.add(new Deadline(description, by));
                    storage.save(tasks);
                    ui.showAddedTask(tasks.get(tasks.size() - 1));
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
                    ui.showAddedTask(tasks.get(tasks.size() - 1));
                } else {
                    throw new SherlockException("That command is not in my casebook. Try another clue.");
                }
            } catch (DateTimeParseException exception) {
                ui.showError("Enter deadline dates in yyyy-MM-dd format, for example 2019-10-15.");
            } catch (SherlockException | IOException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    /**
     * Loads tasks, showing an error and continuing with an empty list if storage cannot be read.
     *
     * @return saved tasks, or an empty list after a loading error
     */
    private TaskList loadTasks() {
        try {
            return storage.load();
        } catch (IOException | SherlockException | DateTimeParseException exception) {
            ui.showLoadingError(exception.getMessage());
            return new TaskList(100);
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
