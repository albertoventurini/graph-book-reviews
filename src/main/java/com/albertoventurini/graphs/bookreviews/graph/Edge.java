package com.albertoventurini.graphs.bookreviews.graph;

public class Edge extends GraphElement {
    public final Node source;
    public final Node target;

    public Edge(final String label, final Node source, final Node target) {
        super(label);
        this.source = source;
        this.target = target;
    }
}
