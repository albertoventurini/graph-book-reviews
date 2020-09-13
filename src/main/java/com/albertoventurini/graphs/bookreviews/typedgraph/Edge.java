package com.albertoventurini.graphs.bookreviews.typedgraph;

import java.util.HashMap;
import java.util.Map;

public abstract class Edge<TSource extends Node, TTarget extends Node> {

    private final TSource source;
    private final TTarget target;

    public Edge(final TSource source, final TTarget target) {
        this.source = source;
        this.target = target;
    }
}
