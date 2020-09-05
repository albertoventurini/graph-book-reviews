package com.albertoventurini.graphs.bookreviews.csv;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class BookReviewsCsvParser {

    private static final char SEPARATOR = ';';
    private static final Charset CHARSET = ISO_8859_1;

    public record ParseResult(List<Book> books, List<BookRating> bookRatings, List<User> users) {}

    public ParseResult parse(
            final String bookFilePath,
            final String bookRatingFilePath,
            final String userFilePath) {

        return new ParseResult(
                parseBooks(bookFilePath),
                parseBookRatings(bookRatingFilePath),
                parseUsers(userFilePath));
    }

    private List<Book> parseBooks(final String bookFilePath) {
        try {
            return readLines(bookFilePath).stream().skip(1).map(tokens -> {
                final String isbn = tokens.get(0);
                final String title = tokens.get(1);
                final String author = tokens.get(2);
                final int yearOfPublication = Integer.parseInt(tokens.get(3));
                final String publisher = tokens.get(4);

                return new Book(isbn, title, author, yearOfPublication, publisher);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new CsvParseException("Error parsing book file " + bookFilePath, e);
        }
    }

    private List<BookRating> parseBookRatings(final String bookRatingFilePath) {
        try {
            return readLines(bookRatingFilePath).stream().skip(1).map(tokens -> {
                final String userId = tokens.get(0);
                final String isbn = tokens.get(1);
                final int rating = Integer.parseInt(tokens.get(2));

                return new BookRating(userId, isbn, rating);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new CsvParseException("Error parsing book rating file " + bookRatingFilePath, e);
        }
    }

    private List<User> parseUsers(final String userFilePath) {
        try {
            return readLines(userFilePath).stream().skip(1).map(tokens -> {
                final String userId = tokens.get(0);
                final String location = tokens.get(1);
                final String ageString = tokens.get(2);
                final Integer age = "NULL".equals(ageString) ? null : Integer.parseInt(ageString);

                return new User(userId, location, age);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new CsvParseException("Error parsing user file " + userFilePath, e);
        }
    }

    private List<List<String>> readLines(final String filePath) throws Exception {
        final String fileString = Files.readString(Path.of(filePath), CHARSET);

        final char[] charArr = fileString.toCharArray();

        int start = 0;
        int end = 0;

        boolean inQuotes = false;

        final List<List<String>> lines = new ArrayList<>();
        final List<String> line = new ArrayList<>();

        int i = 0;
        while (i < charArr.length) {
            if ((charArr[i] == '\n' || charArr[i] == '\r') && !inQuotes) {

                if (end < start) {
                    end = i;
                }
                line.add(String.valueOf(Arrays.copyOfRange(charArr, start, end)));

                lines.add(new ArrayList<>(line));
                line.clear();

                if (charArr[i] == '\r' && i < charArr.length - 1 && charArr[i+1] == '\n') {
                    i++;
                }

            } else if (charArr[i] == '"' && (i == 0 || charArr[i-1] != '\\')) {
                if (!inQuotes) {
                    start = i+1;
                } else {
                    end = i;
                }

                inQuotes = !inQuotes;
            } else if (charArr[i] == SEPARATOR && !inQuotes) {
                if (end < start) {
                    end = i;
                }

                line.add(String.valueOf(Arrays.copyOfRange(charArr, start, end)));
                start = i+1;
            }

            i++;
        }

        return lines;
    }
}
