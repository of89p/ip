package Yokohama.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class Todo {
    public String name;
    protected boolean done;

    Todo(String name, boolean done) {
        this.name = name;
        this.done = done;
    }

    public boolean markComplete() {
        if (this.done) {
            return false;
        } else {
            done = true;
            return true;
        }
    }

    public boolean unmarkComplete() {
        if (!this.done) {
            return false;
        } else {
            this.done = false;
            return true;
        }
    }

    /**
     * Returns whether this task's description contains the specified search keyword.
     * Matching is case-insensitive.
     *
     * @param keyword Keyword to search for.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean hasKeyword(String keyword) {
        return name.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    protected static String convertFromLocalDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM/dd/yyyy HHmm");
        try {
            return dateTime.format(formatter);
        } catch (Exception e) {
            throw e;
        }
    }

    protected static String formatForUser(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
        return dateTime.format(formatter);
    }

    abstract public String toDbString();

    @Override
    public String toString() {
        return this.done ? String.format("[ X ] %s", this.name) : String.format("[  ] %s", this.name);
    }
}
