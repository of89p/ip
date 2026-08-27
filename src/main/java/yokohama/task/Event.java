package yokohama.task;

import java.time.LocalDateTime;

/**
 * Represents a task scheduled between a start and end date and time.
 */
public class Event extends Todo {
    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Creates an event task with the specified description, completion state, start, and end.
     *
     * @param description Description of the task.
     * @param isDone Whether the task is complete.
     * @param start Start date and time of the event.
     * @param end End date and time of the event.
     */
    public Event(String description, boolean isDone, LocalDateTime start, LocalDateTime end) {
        super(description, isDone);
        this.start = start;
        this.end = end;
    }

    @Override
    public String toDbString() {
        return String.format("E | %s | %s | %s | %s\n", isDone() ? "1" : "0", getDescription(), start, end);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (by: " + convertFromLocalDateTime(start)
                + ", to: " + convertFromLocalDateTime(end) + ")";
    }
}
