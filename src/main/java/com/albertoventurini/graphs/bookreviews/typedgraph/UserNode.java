package com.albertoventurini.graphs.bookreviews.typedgraph;

public class UserNode extends Node {

    private final int age;

    public UserNode(final String userId, final int age) {
        super(buildId(userId));
        this.age = age;
    }

    public static String buildId(final String userId) {
        return "user:" + userId;
    }
}
