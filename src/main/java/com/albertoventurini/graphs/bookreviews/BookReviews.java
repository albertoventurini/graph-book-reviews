package com.albertoventurini.graphs.bookreviews;

import com.albertoventurini.graphs.bookreviews.csv.BookReviewsCsvParser;
import com.albertoventurini.graphs.bookreviews.graph.BookReviewsGraph;
import com.albertoventurini.graphs.bookreviews.graph.Queries;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BookReviews {

    public static void main(final String[] args) {
        final BookReviewsCsvParser bookReviewsCsvParser = new BookReviewsCsvParser();

        final var parseResult = bookReviewsCsvParser.parse(
                "data/BX-Books.csv",
                "data/BX-Book-Ratings.csv",
                "data/BX-Users.csv");

        final var graph = new BookReviewsGraph(parseResult);

        final List<Pair<String, Integer>> authorsByReviews = Queries.getAuthorsByNumberOfReviews(graph);

        System.out.println("Top ten authors by number of reviews:\n" +
                authorsByReviews
                        .stream()
                        .limit(10)
                        .map(p -> String.format("%s %s", p.first, p.second))
                        .collect(Collectors.joining("\n")));

        final double avg = Queries.getAverageRatingsByAuthor(graph, "Dan Brown");

        System.out.println("\n\nAverage ratings for top ten authors:\n" +
                authorsByReviews
                        .stream()
                        .limit(10)
                        .map(p -> String.format("%s %f", p.first, Queries.getAverageRatingsByAuthor(graph, p.first)))
                        .collect(Collectors.joining("\n")));

        System.out.println(Queries.getBooksReviewedByUsersInState(graph, "california"));

        System.out.println("\n\nTop ten authors by ratings:\n" +
                Queries.getAuthorsByAverageRating(graph)
                        .stream()
                        .limit(10)
                        .map(p -> String.format("%s %s", p.first, p.second))
                        .collect(Collectors.joining("\n")));

        System.out.println("\n\nBooks reviewed by users in Italy (top 10):\n" +
                Queries.getBooksReviewedByUsersInCountry(graph, "italy")
                    .stream()
                    .limit(10).collect(Collectors.joining("\n")));

        System.out.println(Queries.getAverageAgeByBookTitle(graph, "Dracula"));

    }
}
