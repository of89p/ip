package Yokohama.task;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeadlineTest {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy HHmm");
    LocalDateTime byConverted = LocalDateTime.parse("2/12/2019 1800", formatter);

    @Test
    public void toDbString_taskNotDone_correctFormat() {
        Deadline deadline = new Deadline("return book", false, byConverted);
        assertEquals("D | 0 | return book | 2019-02-12T18:00 \n", deadline.toDbString());
    }

    @Test
    public void toDbString_taskDone_correctFormat() {
        Deadline deadline = new Deadline("return book", true, byConverted);
        assertEquals("D | 1 | return book | 2019-02-12T18:00 \n", deadline.toDbString());
    }

    @Test
    public void toString_validTask_correctFormat() {
        Deadline task = new Deadline("return book", false, byConverted);

        assertEquals("[D][  ] return book (by: Feb 12 2019, 6:00 PM)", task.toString());
    }
}