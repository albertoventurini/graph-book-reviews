package com.albertoventurini.graphs.bookreviews.csv;

public class CsvParseException extends RuntimeException {

    public CsvParseException(final String message) {
        super(message);
    }

    public CsvParseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
