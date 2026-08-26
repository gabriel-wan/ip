package sherlock.task;

/**
 * Represents a task that occurs during a specified time period.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete task that occurs during the given period.
     *
     * @param description text describing the task
     * @param from event start text
     * @param to event end text
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + from + " | " + to;
    }
}
