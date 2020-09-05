package com.albertoventurini.graphs.bookreviews.graph.exceptions;

public class DuplicateNodeException extends RuntimeException {

    public DuplicateNodeException(final String nodeId) {
        super("Duplicate node found: " + nodeId);
    }
}
