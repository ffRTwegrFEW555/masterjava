package ru.javaops.masterjava.exceptions;

/**
 * @author Vadim Gamaliev <a href="mailto:gamaliev-vadim@yandex.com">gamaliev-vadim@yandex.com</a>
 */
public class BreakException extends RuntimeException {
    public BreakException(final String message) {
        super(message);
    }
}
