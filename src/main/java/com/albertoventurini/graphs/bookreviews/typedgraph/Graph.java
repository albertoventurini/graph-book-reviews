package com.albertoventurini.graphs.bookreviews.typedgraph;

import com.albertoventurini.graphs.bookreviews.graph.MapSet;
import com.albertoventurini.graphs.bookreviews.graph.exceptions.DuplicateNodeException;
import com.albertoventurini.graphs.bookreviews.graph.exceptions.NodeNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class Graph {

    private final Map<String, Node> nodeIdToNode = new HashMap<>();

    private final MapSet<Class<?>, Node> nodeClassesToNodes = new MapSet<>();

    public void addNode(final Node node) {
        if (nodeIdToNode.containsKey(node.id)) {
            throw new DuplicateNodeException(node.id);
        }

        nodeIdToNode.put(node.id, node);
        nodeClassesToNodes.put(node.getClass(), node);
    }

    public void addNodeIfAbsent(final Node node) {
        if (!nodeIdToNode.containsKey(node.id)) {
            nodeIdToNode.put(node.id, node);
            nodeClassesToNodes.put(node.getClass(), node);
        }
    }

    public Node getNode(final String id) {
        return nodeIdToNode.get(id);
    }

    public Optional<Node> getOptionalNode(final String id) {
        return Optional.ofNullable(nodeIdToNode.get(id));
    }

    public Node getNodeOrThrow(final String id) {
        final Node node = nodeIdToNode.get(id);
        if (node == null) {
            throw new NodeNotFoundException(id);
        }
        return node;
    }

    public Set<Node> getNodesByClass(final Class<? extends Node> nodeClass) {
        return nodeClassesToNodes.get(nodeClass);
    }

}
