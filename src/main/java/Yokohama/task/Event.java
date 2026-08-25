package Yokohama.task;

import java.time.LocalDateTime;

public class Event extends Todo {
    protected LocalDateTime by;
    protected LocalDateTime to;

    public Event(String description, boolean done, LocalDateTime by, LocalDateTime to) {
        super(description, done);
        this.by = by;
        this.to = to;
    }

    @Override
    public String toDbString() {
        return String.format("E | %s | %s | %s | %s\n", this.done ? "1" : "0", this.name, this.by.toString(), this.to.toString());
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (by: " + super.convertFromLocalDateTime(this.by) + ", to: " + super.convertFromLocalDateTime(this.to) + ")";
    }
}
