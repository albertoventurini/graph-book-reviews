package com.albertoventurini.graphs.bookreviews.graph;

import java.util.HashMap;
import java.util.Map;

/** Represents a graph element with a label and key-value properties */
public class GraphElement {
    public final String label;
    public final Map<String, Object> properties = new HashMap<>();

    public GraphElement(final String label) {
        this.label = label;
    }
}
