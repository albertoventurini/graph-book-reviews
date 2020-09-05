package com.albertoventurini.graphs.bookreviews.graph;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class Query {

    private final Graph graph;

    public Query(final Graph graph) {
        this.graph = graph;
    }

    public Nodes match(final String id) {
        return new Nodes(graph.getOptionalNode(id).stream());
    }

    public Nodes withLabel(final String label) {
        return new Nodes(graph.getNodesByLabel(label).stream());
    }

    public static class Nodes {
        public final Stream<Node> nodes;

        public Nodes(final Stream<Node> nodes) {
            this.nodes = nodes;
        }

        public Relationships out(final String relationshipLabel) {
            return new Relationships(nodes.flatMap(n ->
                    n.outgoingEdges.stream().filter(r -> r.label.equals(relationshipLabel))));
        }

        public Relationships in(final String relationshipLabel) {
            return new Relationships(nodes.flatMap(n ->
                    n.incomingEdges.stream().filter(r -> r.label.equals(relationshipLabel))));
        }

        public Nodes where(final Predicate<Node> predicate) {
            return new Nodes(nodes.filter(predicate));
        }

        public <T> Nodes where(final String propertyName, final Class<T> clazz, final Predicate<T> predicate) {
            return new Nodes(nodes.filter(n -> predicate.test(clazz.cast(n.properties.get(propertyName)))));
        }

        public Stream<Node> stream() {
            return nodes;
        }
    }

    public static class Relationships {
        final Stream<Edge> relationships;

        public Relationships(final Stream<Edge> relationships) {
            this.relationships = relationships;
        }

        public Nodes toNodes() {
            return new Nodes(relationships.map(r -> r.target));
        }

        public Nodes toNodes(final String label) {
            return new Nodes(relationships.filter(r -> r.target.label.equals(label)).map(r -> r.target));
        }

        public Nodes fromNodes() {
            return new Nodes(relationships.map(r -> r.source));
        }

        public Nodes fromNodes(final String label) {
            return new Nodes(relationships.filter(r -> r.source.label.equals(label)).map(r -> r.source));
        }

        public Relationships where(final Predicate<Edge> predicate) {
            return new Relationships(relationships.filter(predicate));
        }
    }

}
