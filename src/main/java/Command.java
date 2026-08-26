/**
 * Represents a validated action that Sherlock can perform.
 */
class Command {
    /** Identifies the action represented by a command. */
    enum Type {
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

    static Command of(Type type) {
        return new Command(type, 0, null);
    }

    static Command withTaskNumber(Type type, int taskNumber) {
        return new Command(type, taskNumber, null);
    }

    static Command withTask(Task task) {
        return new Command(Type.ADD, 0, task);
    }

    Type getType() {
        return type;
    }

    int getTaskNumber() {
        return taskNumber;
    }

    Task getTask() {
        return task;
    }
}
