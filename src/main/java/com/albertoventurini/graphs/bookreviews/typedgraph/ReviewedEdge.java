package com.albertoventurini.graphs.bookreviews.typedgraph;

public class ReviewedEdge extends Edge<UserNode, BookNode> {

    private final int rating;

    public ReviewedEdge(final UserNode user, final BookNode book, final int rating) {
        super(user, book);
        this.rating = rating;
    }
}
