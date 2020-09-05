package com.albertoventurini.graphs.bookreviews.graph;

import java.util.HashMap;
import java.util.Map;

public class Edge {

    public final String label;
    public final Node source;
    public final Node target;

    public final Map<String, Object> properties;

    public Edge(final String label, final Node source, final Node target) {
        this.label = label;
        this.source = source;
        this.target = target;

        properties = new HashMap<>();
    }
}
