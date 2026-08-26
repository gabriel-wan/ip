package sherlock.command;

import sherlock.task.Task;

/**
 * Represents a validated action that Sherlock can perform.
 */
public class Command {
    /** Identifies the action represented by a command. */
    public enum Type {
        BYE,
        LIST,
        FIND,
        MARK,
        UNMARK,
        DELETE,
        ADD
    }

    private final Type type;
    private final int taskNumber;
    private final Task task;
    private final String keyword;

    private Command(Type type, int taskNumber, Task task, String keyword) {
        this.type = type;
        this.taskNumber = taskNumber;
        this.task = task;
        this.keyword = keyword;
    }

    public static Command of(Type type) {
        return new Command(type, 0, null, null);
    }

    public static Command withTaskNumber(Type type, int taskNumber) {
        return new Command(type, taskNumber, null, null);
    }

    public static Command withTask(Task task) {
        return new Command(Type.ADD, 0, task, null);
    }

    /**
     * Creates a find command that searches task descriptions for a keyword.
     *
     * @param keyword text to find in task descriptions
     * @return find command for the keyword
     */
    public static Command withKeyword(String keyword) {
        return new Command(Type.FIND, 0, null, keyword);
    }

    public Type getType() {
        return type;
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    public Task getTask() {
        return task;
    }

    /**
     * Returns the keyword carried by a find command, when it has one.
     *
     * @return task-description keyword
     */
    public String getKeyword() {
        return keyword;
    }
}
