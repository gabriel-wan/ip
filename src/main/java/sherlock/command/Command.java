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
        MARK,
        UNMARK,
        DELETE,
        ADD
    }

    private final Type type;
    private final int taskNumber;
    private final Task task;

    private Command(Type type, int taskNumber, Task task) {
        this.type = type;
        this.taskNumber = taskNumber;
        this.task = task;
    }

    /**
     * Creates a command that has no task-specific argument.
     *
     * @param type action to perform
     * @return command for the action
     */
    public static Command of(Type type) {
        return new Command(type, 0, null);
    }

    /**
     * Creates a command that refers to an existing one-based task number.
     *
     * @param type action to perform
     * @param taskNumber one-based number of the target task
     * @return command for the action and task number
     */
    public static Command withTaskNumber(Type type, int taskNumber) {
        return new Command(type, taskNumber, null);
    }

    /**
     * Creates an add command for a newly created task.
     *
     * @param task task to add
     * @return add command for the task
     */
    public static Command withTask(Task task) {
        return new Command(Type.ADD, 0, task);
    }

    /**
     * Returns the action represented by this command.
     *
     * @return command action
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the command's one-based task number, when it has one.
     *
     * @return referenced task number
     */
    public int getTaskNumber() {
        return taskNumber;
    }

    /**
     * Returns the task carried by an add command, when it has one.
     *
     * @return task to add
     */
    public Task getTask() {
        return task;
    }
}
