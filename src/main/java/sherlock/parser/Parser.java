package sherlock.parser;

import sherlock.command.Command;
import sherlock.exception.SherlockException;
import sherlock.task.Deadline;
import sherlock.task.Event;
import sherlock.task.Todo;

/**
 * Converts user-entered text into validated Sherlock commands.
 */
public class Parser {
    /**
     * Parses one command using the current task count to validate task references.
     *
     * @param input full command entered by the user
     * @param taskCount number of tasks currently stored
     * @return the corresponding command
     * @throws SherlockException if the command is incomplete or invalid
     */
    public Command parse(String input, int taskCount) throws SherlockException {
        if (input.equals("bye")) {
            return Command.of(Command.Type.BYE);
        } else if (input.equals("list")) {
            return Command.of(Command.Type.LIST);
        } else if (input.equals("find") || input.startsWith("find ")) {
            String keyword = requireText(input.substring(4), "I need a keyword to search the casebook.");
            return Command.withKeyword(keyword);
        } else if (input.equals("mark") || input.startsWith("mark ")) {
            return Command.withTaskNumber(Command.Type.MARK, parseTaskNumber(input.substring(4), taskCount));
        } else if (input.equals("unmark") || input.startsWith("unmark ")) {
            return Command.withTaskNumber(Command.Type.UNMARK, parseTaskNumber(input.substring(6), taskCount));
        } else if (input.equals("delete") || input.startsWith("delete ")) {
            return Command.withTaskNumber(Command.Type.DELETE, parseTaskNumber(input.substring(6), taskCount));
        } else if (input.equals("todo") || input.startsWith("todo ")) {
            String description = requireText(input.substring(4), "I need a case description before I can add it.");
            return Command.withTask(new Todo(description));
        } else if (input.equals("deadline") || input.startsWith("deadline ")) {
            String[] details = input.substring(8).trim().split(" /by ", 2);
            if (details.length != 2) {
                throw new SherlockException("A deadline must include /by followed by a time.");
            }
            String description = requireText(details[0], "The description of a deadline cannot be empty.");
            String deadlineDate = requireText(details[1], "The time of a deadline cannot be empty.");
            return Command.withTask(new Deadline(description, deadlineDate));
        } else if (input.equals("event") || input.startsWith("event ")) {
            String[] details = input.substring(5).trim().split(" /from | /to ", 3);
            if (details.length != 3) {
                throw new SherlockException("An event must include /from and /to times.");
            }
            String description = requireText(details[0], "The description of an event cannot be empty.");
            String startTime = requireText(details[1], "The start time of an event cannot be empty.");
            String endTime = requireText(details[2], "The end time of an event cannot be empty.");
            return Command.withTask(new Event(description, startTime, endTime));
        }
        throw new SherlockException("That command is not in my casebook. Try another clue.");
    }

    /**
     * Validates and converts a one-based task number supplied in a command.
     *
     * @param input task number text
     * @param taskCount number of tasks currently stored
     * @return the validated task number
     * @throws SherlockException if the input is not a valid task number
     */
    private int parseTaskNumber(String input, int taskCount) throws SherlockException {
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
    private String requireText(String text, String errorMessage) throws SherlockException {
        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            throw new SherlockException(errorMessage);
        }
        return trimmedText;
    }
}
