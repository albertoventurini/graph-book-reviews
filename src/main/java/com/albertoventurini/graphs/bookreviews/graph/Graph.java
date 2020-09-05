package com.albertoventurini.graphs.bookreviews.graph;

import com.albertoventurini.graphs.bookreviews.graph.exceptions.DuplicateNodeException;
import com.albertoventurini.graphs.bookreviews.graph.exceptions.NodeNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Graph {

    private final Map<String, Node> nodeIdToNode = new HashMap<>();

    private final MapSet<String, Node> nodeLabelToNodes = new MapSet<>();

    public Node addNode(
            final String id,
            final String label) {

        if (nodeIdToNode.containsKey(id)) {
            throw new DuplicateNodeException(id);
        }

        final Node n = new Node(id, label);
        nodeIdToNode.put(id, n);
        nodeLabelToNodes.put(label, n);
        return n;
    }

    public Node addNodeIfAbsent(
            final String id,
            final String label) {
        Node n = nodeIdToNode.get(id);

        if (n == null) {
            n = new Node(id, label);
            nodeIdToNode.put(id, n);
            nodeLabelToNodes.put(label, n);
        }

        return n;
    }

    public Edge addEdge(
            final String label,
            final String fromNodeId,
            final String toNodeId) {

        final Node fromNode = getNodeOrThrow(fromNodeId);
        final Node toNode = getNodeOrThrow(toNodeId);

        final Edge e = new Edge(label, fromNode, toNode);
        fromNode.addOutgoingEdge(e);
        toNode.addIncomingEdge(e);

        return e;
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

    public Set<Node> getNodesByLabel(final String label) {
        return nodeLabelToNodes.get(label);
    }

}
