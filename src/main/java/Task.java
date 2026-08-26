/**
 * Represents one task and whether it has been completed.
 */
public abstract class Task {
    private final String description;
    private TaskStatus status;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task
     */
    protected Task(String description) {
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the letter that identifies this task type in the text UI.
     *
     * @return the task type icon
     */
    protected abstract String getTypeIcon();

    /**
     * Returns this task in the compact format used in Sherlock's data file.
     *
     * @return persistent representation of this task
     */
    public String toFileString() {
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
