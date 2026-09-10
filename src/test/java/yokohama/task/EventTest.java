package yokohama.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class EventTest {
    private final Event event = new Event("conference", false,
            LocalDateTime.of(2026, 9, 10, 9, 0),
            LocalDateTime.of(2026, 9, 12, 17, 0));

    @Test
    public void occursOn_dateWithinMultiDayEvent_true() {
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 11)));
    }

    @Test
    public void occursOn_dateOutsideEvent_false() {
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 13)));
    }
}
