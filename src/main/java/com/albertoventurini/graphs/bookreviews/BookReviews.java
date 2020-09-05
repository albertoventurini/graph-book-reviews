package com.albertoventurini.graphs.bookreviews;

import com.albertoventurini.graphs.bookreviews.csv.BookReviewsCsvParser;
import com.albertoventurini.graphs.bookreviews.graph.BookReviewsGraph;
import com.albertoventurini.graphs.bookreviews.graph.Queries;

import java.util.List;

public class BookReviews {

    public static void main(final String[] args) {
        final BookReviewsCsvParser bookReviewsCsvParser = new BookReviewsCsvParser();

        final var parseResult = bookReviewsCsvParser.parse(
                "data/BX-Books.csv",
                "data/BX-Book-Ratings.csv",
                "data/BX-Users.csv");

        final var graph = new BookReviewsGraph(parseResult);

        final List<Pair<String, Integer>> authorsByReviews = Queries.getAuthorsByNumberOfReviews(graph);

        final double avg = Queries.getAverageRatingsByAuthor(graph, "Dan Brown");

        int i = 0;
    }
}
