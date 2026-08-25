package Yokohama.task;

import java.time.LocalDateTime;

public class Deadline extends Todo {
    protected LocalDateTime by;

    public Deadline(String description, boolean done, LocalDateTime by) {
        super(description, done);
        this.by = by;
    }

    @Override
    public String toDbString() {
        return String.format("D | %s | %s | %s \n", this.done ? "1" : "0", this.name, this.by.toString());
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatForUser(this.by) + ")";
    }
}
