package com.albertoventurini.graphs.bookreviews.typedgraph;

public class BookNode extends Node {

    public final String isbn;
    public final String title;

    public BookNode(final String isbn, final String title) {
        super(buildId(isbn));
        this.isbn = isbn;
        this.title = title;
    }

    public static String buildId(final String isbn) {
        return "book:" + isbn;
    }
}