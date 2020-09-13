package com.albertoventurini.graphs.bookreviews.typedgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class Node {

    public final String id;
    public final List<Edge<?, ?>> outgoingEdges;
    public final List<Edge<?, ?>> incomingEdges;

    public Node(final String id) {
        this.id = id;

        outgoingEdges = new ArrayList<>();
        incomingEdges = new ArrayList<>();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    void addOutgoingEdge(final Edge<?, ?> r) {
        outgoingEdges.add(r);
    }

    void addIncomingEdge(final Edge<?, ?> r) {
        incomingEdges.add(r);
    }

    Stream<Edge<?, ?>> outgoingEdges() {
        return outgoingEdges.stream();
    }

    Stream<Edge<?, ?>> outgoingEdges(final Class<? extends Edge<?, ?>> edgeClass) {
        return outgoingEdges.stream().filter(edgeClass::isInstance);
    }

    Stream<Edge<?, ?>> incomingEdges() {
        return incomingEdges.stream();
    }

    Stream<Edge<?, ?>> incomingEdges(final Class<? extends Edge<?, ?>> edgeClass) {
        return incomingEdges.stream().filter(edgeClass::isInstance);
    }

}
