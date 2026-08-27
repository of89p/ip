package yokohama.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void toDbString_taskNotDone_correctFormat() {
        Task task = new Task("buy groceries", false);
        assertEquals("T | 0 | buy groceries\n", task.toDbString());
    }

    @Test
    public void toDbString_taskDone_correctFormat() {
        Task task = new Task("read book", true);
        assertEquals("T | 1 | read book\n", task.toDbString());
    }

    @Test
    public void toString_validTask_correctFormat() {
        Task task = new Task("fix bug", false);

        assertEquals("[T][  ] fix bug", task.toString());
    }
}
