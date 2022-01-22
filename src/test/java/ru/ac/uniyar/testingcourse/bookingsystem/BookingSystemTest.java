package ru.ac.uniyar.testingcourse.bookingsystem;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.assertj.core.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class BookingSystemTest {

    private BookingSystem bookingSystem = new BookingSystem();
    
    public BookingSystemTest() {
    }

    @Test
    public void bookedHoursListShouldBeEmptyAfterCreation() {
        List<Integer> bookedHours = bookingSystem.getBookedHoursList();
        assertThat(bookedHours).isEmpty();
    }
    
    @Test
    public void possibleToBookOneInterval() {
        assertThat(bookingSystem.book("user", 12, 14)).isTrue();
        List<Integer> bookedHours = bookingSystem.getBookedHoursList();
        assertThat(bookedHours).containsExactly(12, 13);
    }

    // absorbed by impossibleToBookIntervalBeyondBoundaries
    @Test
    public void impossibleToBookIntervalEarlierThan8am() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> bookingSystem.book("user", 4, 7));
        assertThat(bookingSystem.getBookedHoursList()).isEmpty();
    }

    // absorbed by impossibleToBookIntervalBeyondBoundaries
    @Test
    public void impossibleToBookIntervalLaterThan8pm() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> bookingSystem.book("user", 20, 22));
        assertThat(bookingSystem.getBookedHoursList()).isEmpty();
    }

    @Test
    @Parameters({"4, 7", "20, 22"})
    public void impossibleToBookIntervalBeyondBoundaries(int from, int till) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> bookingSystem.book("user", from, till));
        assertThat(bookingSystem.getBookedHoursList()).isEmpty();
    }

    private Object[] dataForTwoNonCrossingInvervalsTest() {
        return new Object[] {
            new Object[] { 10, 12, 14, 17, Arrays.asList(10, 11, 14, 15, 16) },
            new Object[] { 10, 12, 12, 14, Arrays.asList(10, 11, 12, 13) },
        };
    }
    
    @Test
    @Parameters(method = "dataForTwoNonCrossingInvervalsTest")
    public void possibleToBookTwoNonCrossingInvervals(int from1, int till1,
            int from2, int till2, List<Integer> result) {
        bookingSystem.book("user", from1, till1);
        assertThat(bookingSystem.book("user", from2, till2)).isTrue();
        assertThat(bookingSystem.getBookedHoursList()).isEqualTo(result);
    }
    
    @Test
    public void impossibleToBookCrossingIntervals() {
        bookingSystem.book("user", 10, 18);
        assertThat(bookingSystem.book("user", 9, 11)).isFalse();
        assertThat(bookingSystem.getBookedHoursList()).doesNotContain(9);
    }
    
    @Test
    public void possibleToUnbookPreviouslyBookedInterval() {
        bookingSystem.book("user", 9, 16);
        bookingSystem.unbook("user", 9, 16);
        assertThat(bookingSystem.getBookedHoursList()).isEmpty();
    }
    
    @Test
    public void possibleToUnbookPreviouslyBookedIntervalPartially() {
        bookingSystem.book("user", 9, 16);
        bookingSystem.unbook("user", 10, 14);
        assertThat(bookingSystem.getBookedHoursList()).containsExactly(9, 14, 15);
    }
    
    @Test
    public void impossibleToUnbookIntervalBeyondBoundaries() {
        bookingSystem.book("user", 19, 20);
        List<Integer> bookedHours = bookingSystem.getBookedHoursList();
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> bookingSystem.unbook("user", 19, 21));
        assertThat(bookingSystem.getBookedHoursList()).isEqualTo(bookedHours);
    }
    
    @Test
    public void impossibleToUnbookNotPreviouslyBookedInterval() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> bookingSystem.unbook("user", 10, 14));
    }
    
    @Test
    public void impossibleToUnbookIntervalContainingBookedAndUnbookedSubintervals() {
        bookingSystem.book("user", 9, 16);
        List<Integer> bookedHours = bookingSystem.getBookedHoursList();
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> bookingSystem.unbook("user", 12, 18));
        assertThat(bookingSystem.getBookedHoursList()).isEqualTo(bookedHours);
    }
    
    @Test
    public void impossibleToUnbookIntervalBookedByAnotherUser() {
        bookingSystem.book("user", 9, 14);
        List<Integer> bookedHours = bookingSystem.getBookedHoursList();
        Assertions.assertThatExceptionOfType(BookingSystem.BookedByAnotherUserException.class)
                .isThrownBy(() -> bookingSystem.unbook("other user", 9, 14));
        assertThat(bookingSystem.getBookedHoursList()).isEqualTo(bookedHours);
    }
}
