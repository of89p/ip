package yokohama.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Converts date-time values between strings and {@link LocalDateTime} objects.
 */
public class DateTimeHandler {

    /** Formatter for the application's date-time input and display format. */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy HHmm");

    /**
     * Converts a date-time string in the application's standard format to a {@code LocalDateTime}.
     *
     * @param dateTime Date-time string in {@code M/d/yyyy HHmm} format.
     * @return Parsed date-time value.
     */
    public static LocalDateTime convertToLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
    }

    /**
     * Returns a date-time value in the application's standard readable format.
     *
     * @param dateTime Date-time value to format.
     * @return Date-time string in {@code M/d/yyyy HHmm} format.
     */
    public static String formatToReadable(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
