package com.albertoventurini.graphs.bookreviews.graph;

import com.albertoventurini.graphs.bookreviews.csv.Book;
import com.albertoventurini.graphs.bookreviews.csv.BookRating;
import com.albertoventurini.graphs.bookreviews.csv.BookReviewsCsvParser;
import com.albertoventurini.graphs.bookreviews.csv.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookReviewsGraph extends Graph {

    public static final String NODE_BOOK = "book";
    public static final String NODE_USER = "user";
    public static final String NODE_PUBLISHER = "publisher";
    public static final String NODE_AUTHOR = "author";
    public static final String NODE_CITY = "city";
    public static final String NODE_STATE = "state";
    public static final String NODE_COUNTRY = "country";

    public static final String EDGE_PUBLISHED_BY = "publishedBy";
    public static final String EDGE_WRITTEN_BY = "writtenBy";
    public static final String EDGE_IN_CITY = "inCity";
    public static final String EDGE_IN_STATE = "inState";
    public static final String EDGE_IN_COUNTRY = "inCountry";
    public static final String EDGE_REVIEWED = "reviewed";

    public MapSet<String, Node> countriesByName = new MapSet<>();
    public MapSet<String, Node> statesByName = new MapSet<>();
    public MapSet<String, Node> citiesByName = new MapSet<>();

    public BookReviewsGraph(final BookReviewsCsvParser.ParseResult source) {
        source.books().forEach(b -> {
            addBookNode(b);
            addPublisherNode(b);
            addAuthorNode(b);
        });

        source.users().forEach(this::addUserNode);

        source.bookRatings().forEach(this::addBookRating);
    }

    private void addBookNode(final Book book) {
        final Node node = addNode(book.isbn(), NODE_BOOK);
        node.properties.put("isbn", book.isbn());
        node.properties.put("title", book.title());
    }

    private void addPublisherNode(final Book book) {
        addNodeIfAbsent(book.publisher(), NODE_PUBLISHER);

        final Edge edge = addEdge(EDGE_PUBLISHED_BY, book.isbn(), book.publisher());
        edge.properties.put("year", book.yearOfPublication());
    }

    private void addAuthorNode(final Book book) {
        final Node node = addNodeIfAbsent(book.author(), NODE_AUTHOR);
        node.properties.put("name", book.author());
        addEdge(EDGE_WRITTEN_BY, book.isbn(), book.author());
    }

    private void addBookRating(final BookRating bookRating) {
        final String userId = buildUserId(bookRating.userId());
        if (getNode(userId) == null) {
            return;
        }
        if (getNode(bookRating.isbn()) == null) {
            return;
        }
        final Edge edge = addEdge(EDGE_REVIEWED, userId, bookRating.isbn());
        edge.properties.put("rating", bookRating.rating());
    }

    private void addUserNode(final User user) {
        final Node userNode = addNode(buildUserId(user), NODE_USER);
        userNode.properties.put("age", user.age());

        addUserLocation(user);
    }

    private void addUserLocation(final User user) {
        final List<String> locationTokens = Arrays.stream(user.location().split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        addLocationNodes(locationTokens);

        buildCityId(locationTokens).ifPresent(cityId -> {
            addEdge(EDGE_IN_CITY, buildUserId(user), cityId);
        });
    }

    private void addLocationNodes(final List<String> locationTokens) {
        addCountryIfAbsent(locationTokens);
        addStateIfAbsent(locationTokens);
        addCityIfAbsent(locationTokens);
    }

    private void addCountryIfAbsent(final List<String> locationTokens) {
        buildCountryId(locationTokens).ifPresent(countryId -> {
            if (getNode(countryId) == null) {
                final Node countryNode = addNode(countryId, NODE_COUNTRY);
                countryNode.properties.put("name", locationTokens.get(2));
                countriesByName.put(locationTokens.get(2), countryNode);
            }
        });
    }

    private void addStateIfAbsent(final List<String> locationTokens) {
        buildStateId(locationTokens).ifPresent(stateId -> {
            if (getNode(stateId) == null) {
                final Node stateNode = addNode(stateId, NODE_STATE);
                stateNode.properties.put("name", locationTokens.get(1));
                statesByName.put(locationTokens.get(1), stateNode);

                buildCountryId(locationTokens).ifPresent(countryId -> {
                    addEdge(EDGE_IN_COUNTRY, stateId, countryId);
                });
            }
        });
    }

    private void addCityIfAbsent(final List<String> locationTokens) {
        buildCityId(locationTokens).ifPresent(cityId -> {
            if (getNode(cityId) == null) {
                final Node cityNode = addNode(cityId, NODE_CITY);
                cityNode.properties.put("name", locationTokens.get(0));
                citiesByName.put(locationTokens.get(0), cityNode);

                buildStateId(locationTokens).ifPresent(stateId -> {
                    addEdge(EDGE_IN_STATE, cityId, stateId);
                });
            }
        });
    }

    private String buildUserId(final User user) {
        return buildUserId(user.userId());
    }

    private String buildUserId(final String id) {
        return "user:" + id;
    }

    private Optional<String> buildCityId(final List<String> locationTokens) {
        if (locationTokens.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(String.join(":", locationTokens));
        }
    }

    private Optional<String> buildStateId(final List<String> locationTokens) {
        if (locationTokens.size() != 3) {
            return Optional.empty();
        } else if (locationTokens.get(1).equals("n/a")) {
            return Optional.empty();
        } else {
            return Optional.of(locationTokens.get(1) + ":" + locationTokens.get(2));
        }
    }

    private Optional<String> buildCountryId(final List<String> locationTokens) {
        if (locationTokens.size() != 3) {
            return Optional.empty();
        } else {
            return Optional.of(locationTokens.get(2));
        }
    }
}
