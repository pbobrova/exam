package ru.ac.uniyar.testingcourse.bookingsystem;

import java.nio.file.AccessDeniedException;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/** 
 * Класс, предназначенный для бронирования помещения на определённое 
 * пользователями время.
 */
class BookingSystem {
    private TreeMap<Integer, String> bookedHours = new TreeMap<>();

    /** 
     * Получение часов, на которые выполнено бронирование, в виде упорядоченного
     * списка.
     */
    public List<Integer> getBookedHoursList() {
        return new LinkedList(bookedHours.keySet());
    }

    /**
     * Попытка бронирования помещения на указанное время.
     * @param user идентификатор пользователя.
     * @param from час начала бронирования.
     * @param till час окончания бронирования (в забронированный интервал 
     *         не входит).
     * @return флаг успешности выполнения операции.
     * @throws IllegalArgumentException при попытке бронирования за пределами
     *                                  допустимого интервала (8:00-20:00).
     */
    boolean book(String user, int from, int till) {
        if (from < 8 || till > 20) throw new IllegalArgumentException();
        for (int i = from; i < till; i++) {
            if (bookedHours.containsKey(i)) {
                return false;
            }
        }        
        for (int i = from; i < till; i++) {
            bookedHours.put(i, user);
        }
        return true;
    }

    /**
     * Отмена бронирования помещения на указанное время.
     * @param user идентификатор пользователя.
     * @param from час начала бронирования.
     * @param till час окончания бронирования (в забронированный интервал 
     *         не входит).
     * @throws IllegalArgumentException при попытке бронирования за пределами
     *                                  допустимого интервала (8:00-20:00).
     * @throws BookedByAnotherUserException при попытке отменить бронирование,
     *                                  выполненное другим пользователем.
     */
    void unbook(String user, int from, int till) {
        if (from < 8 || till > 20) throw new IllegalArgumentException();
        for (int i = from; i < till; i++) {
            if (!bookedHours.containsKey(i)) {
                throw new IllegalArgumentException();
            }
            if (!bookedHours.get(i).equals(user)) {
                throw new BookedByAnotherUserException();
            }
        }        
        for (int i = from; i < till; i++) {
            bookedHours.remove(i);
        }        
    }

    /** 
     * Класс исключения, выбрасываемого при попытке отмены бронирования,
     * выпоненного другим пользователем.
     */
    static class BookedByAnotherUserException extends RuntimeException {
    }
    
}
