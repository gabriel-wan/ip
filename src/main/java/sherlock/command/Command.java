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

    public static Command of(Type type) {
        return new Command(type, 0, null);
    }

    public static Command withTaskNumber(Type type, int taskNumber) {
        return new Command(type, taskNumber, null);
    }

    public static Command withTask(Task task) {
        return new Command(Type.ADD, 0, task);
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
}
