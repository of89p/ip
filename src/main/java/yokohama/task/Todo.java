package yokohama.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task with a description and completion state.
 */
public abstract class Todo {
    private final String description;
    private boolean isDone;

    Todo(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Marks this task as complete.
     *
     * @return {@code true} if the task was marked complete, or {@code false} if it already was complete.
     */
    public boolean markComplete() {
        if (isDone) {
            return false;
        }

        isDone = true;
        return true;
    }

    /**
     * Marks this task as incomplete.
     *
     * @return {@code true} if the task was marked incomplete, or {@code false} if it already was incomplete.
     */
    public boolean unmarkComplete() {
        if (!isDone) {
            return false;
        }

        isDone = false;
        return true;
    }

    protected final String getDescription() {
        return description;
    }

    protected final boolean isDone() {
        return isDone;
    }

    protected static String convertFromLocalDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM/dd/yyyy HHmm");
        return dateTime.format(formatter);
    }

    protected static String formatForUser(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
        return dateTime.format(formatter);
    }

    /**
     * Returns whether this task's description contains the specified search keyword.
     * Matching is case-insensitive.
     *
     * @param keyword Keyword to search for.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean hasKeyword(String keyword) {
        return this.description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns this task in the format used by the data file.
     *
     * @return Serialized task representation.
     */
    public abstract String toDbString();

    @Override
    public String toString() {
        return isDone ? String.format("[ X ] %s", description) : String.format("[  ] %s", description);
    }
}
