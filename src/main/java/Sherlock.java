import java.io.IOException;
import java.nio.file.Path;
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
    private final Parser parser;

    /**
     * Creates the application and restores saved tasks where possible.
     *
     * @param filePath location of the task data file
     * @param ui user interface used for all interaction
     */
    Sherlock(Path filePath, Ui ui) {
        this.storage = new Storage(filePath);
        this.ui = ui;
        this.parser = new Parser();
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
            try {
                Command command = parser.parse(ui.readCommand(), tasks.size());
                if (execute(command)) {
                    return;
                }
            } catch (DateTimeParseException exception) {
                ui.showError("Enter deadline dates in yyyy-MM-dd format, for example 2019-10-15.");
            } catch (SherlockException | IOException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    /**
     * Applies a parsed command to Sherlock's task list.
     *
     * @param command command to execute
     * @return whether Sherlock should exit
     * @throws IOException if an updated task list cannot be saved
     */
    private boolean execute(Command command) throws IOException {
        switch (command.getType()) {
        case BYE:
            ui.showGoodbye();
            return true;
        case LIST:
            ui.showTaskList(tasks);
            return false;
        case MARK:
            Task completedTask = tasks.get(command.getTaskNumber() - 1);
            completedTask.markAsDone();
            storage.save(tasks);
            ui.showMarkedAsDone(completedTask);
            return false;
        case UNMARK:
            Task incompleteTask = tasks.get(command.getTaskNumber() - 1);
            incompleteTask.markAsNotDone();
            storage.save(tasks);
            ui.showMarkedAsNotDone(incompleteTask);
            return false;
        case DELETE:
            Task deletedTask = tasks.delete(command.getTaskNumber() - 1);
            storage.save(tasks);
            ui.showDeletedTask(deletedTask, tasks.size());
            return false;
        case ADD:
            tasks.add(command.getTask());
            storage.save(tasks);
            ui.showAddedTask(command.getTask());
            return false;
        default:
            throw new AssertionError("Unhandled command type: " + command.getType());
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

}
