package com.albertoventurini.graphs.bookreviews.graph.exceptions;

public class NodeNotFoundException extends RuntimeException {

    public NodeNotFoundException(final String nodeId) {
        super("Node not found: " + nodeId);
    }
}
