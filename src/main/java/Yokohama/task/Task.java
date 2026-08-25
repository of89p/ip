package Yokohama.task;

public class Task extends Todo {
    public Task(String description, boolean done) {
        super(description, done);
    }

    @Override
    public String toDbString() {
        return String.format("T | %s | %s\n", this.done ? "1" : "0", this.name);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
