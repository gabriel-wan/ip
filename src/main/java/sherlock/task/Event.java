package sherlock.task;

/**
 * Represents a task that occurs during a specified time period.
 */
public class Event extends Task {
    private final String startTime;
    private final String endTime;

    public Event(String description, String startTime, String endTime) {
        super(description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + startTime + " to: " + endTime + ")";
    }

    @Override
    public String toFileString() {
        return super.toFileString() + " | " + startTime + " | " + endTime;
    }
}
