package Yokohama.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for handling conversion between readable string datetime
 * and LocalDatetime object for storage.
 */
public class DateTimeHandler {
    /**
     * Converts user entered deadline to LocalDateTime object.
     * Throws exception when user-entered datetime is not of M/d/yyyy HHmm format.
     *
     * @param dateTime User entered deadline.
     * @return LocalDateTime object to be stored in data file.
     */
    public static LocalDateTime convertToLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy HHmm");
        try {
            return LocalDateTime.parse(dateTime, formatter);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Converts LocalDateTime object back to readable String format.
     *
     * @param dateTime LocalDateTime of a Todo class.
     * @return Readable string datetime.
     */
    public static String formatToReadable (LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy HHmm");
        try {
            return dateTime.format(formatter);
        } catch (Exception e) {
            throw e;
        }
    }
}
