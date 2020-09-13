# Modelling data with graphs

Modelling data is a crucial aspect of software engineering. Choosing appropriate data structures or databases is fundamental to the success of an application or a service.

In this article, I will discuss some techniques related to modelling data domains with graphs. In particular, I will show how **labelled property graphs** can be an effective solution to some of the challenges we sometimes encounter with other models (e.g. relational databases or in-memory key-value stores.)

By the end of this article we will have a simple -- but fully functional -- implementation of an in-memory labelled-property graph in Java. We'll use this graph to run some queries on a sample dataset.

## A brief introduction to property graphs



## Sample domain: book reviews

Before writing this article, I headed over to [kaggle.com](https://kaggle.com) and browsed through some of the data sets available there. Eventually, I picked this [book review data set](https://www.kaggle.com/ruchi798/bookcrossing-dataset) which we'll use as a running example throughout this article.

This data set contains the following CSV files:

- *BX-Users.csv*, with anonymised user data. Each user has a unique id, location and age.
- *BX-Books.csv*, which contains each book's ISBN, title, author, publisher and year of publication. This file also contains links to thumbnail pictures, but we won't use those here.
- *BX-Book-Ratings.csv*, which contains a row for each book review.

If we picture this data set as an entity-relationship (ER) diagram, this is how it would look like:

![ERD](/Users/alberto/Devel/graph-book-reviews/doc/ERD.png)

We can see that some fields are [denormalized](https://en.wikipedia.org/wiki/Denormalization), e.g. `Author`, `Publisher`, and `Location`. While this might or might not be what we eventually want (depending on the performance characteristics of certain queries), at this point let's assume we want our data in [normalized form](https://en.wikipedia.org/wiki/Database_normalization). After normalization, this is our updated ER diagram:

![ERD2](/Users/alberto/Devel/graph-book-reviews/doc/ERD2.png)

Note that we split `Location` into 3 tables (`City`, `State`, and `Country`) because the original field contained strings such as `"San Francisco, California, USA"`.

Now, suppose we store our data into a relational database. As a thought exercise, how easily can we express the following two queries with SQL code?

- **Query 1**: get the average rating for each author.
- **Query 2**: get all books reviewed by users from a specific country.

If you're familiar with SQL, you're probably thinking *joins*: whenever we have relations that span multiple tables, we typically navigate relations by joining pairs of tables via primary and foreign keys.

However, both queries involve a non-trivial amount of joins -- especially the second one. This is not necessarily a bad thing -- relational databases can be quite good at executing joins efficiently -- but it might lead to SQL code that is difficult to read, maintain, and optimize.

Moreover, in order to navigate many-to-many relationships, we had to create *join tables* (e.g. `BookRating` or `BookPublished`). This is a common pattern which, however, adds complexity to the overall schema.

We could denormalize some of our data, but this would have the side-effect of locking us into a specific view of our data and causing our model to be less flexible.

## Book reviews in Java

Now let's suppose we want to store our entire data set in memory. This is a trivial but perfectly valid approach if our application doesn't need to guarantee write consistency, i.e. we never change the data. The sample data set that we got from Kaggle contains less than a million entries, so it will easily fit in memory.

We'll start with some classes, for example:

```
public class Book {
	String isbn;
	String title;
}

public class Author {
	String name;
}
```

Soon, though, we are confronted with a question: how do we establish relationships across these classes?

In the case of `Book` and `Author`, we might consider that a book has one author (let's keep things simple and suppose each book has only one author), and use a direct reference:

```
public class Book {
	String isbn;
	String title;
	Author author;
}
```

This makes it trivial to get the author of a book. However, the reverse (getting all books written by an author) is a more expensive operation, because we need to scan the entire list of books (the cost is linear in the number of books.) For example, if we're looking for all books by Dan Brown, we need to write something like this:

```
var danBrownBooks = books.stream()
                      .filter(b -> b.author.name.equals("Dan Brown"))
											.collect(Collectors.toList());
```

We can improve on this in a couple of different ways. For example, we can store references to books from the `Author` class:

```
public class Author {
  String name;
  List<Book> books;
}
```

or we can use a `Map` to link author names and books:

```
Map<String, List<Book>> booksByAuthorNames = new HashMap<>();
```

However, neither solution "feels" good. Now, every time we want to change a book's author, we have two places to change.

## Labelled property graphs

I would argue that both the relational and the Java class models suffer from the same issue: **they represent relationships as entity data**.

In both cases, not only does an entity contain attributes about itself (e.g. a Book table contains its title and ISBN code) but it also contains data about how it's connected to other entities.

In other words, relationships are tightly coupled to specific entities, which could end up making our queries more complex and our overall model less flexible -- especially when our data has lots of relationships.

Traditionally, this way of representing relationships had the advantage of being space-efficient. [...]

Graph models adopt a different approach: **in a graph, relationships are modelled explicitly and are treated as first-class citizens of the data model**, just like entities.

You may recall from mathematics that a graph is a collection of nodes (also known as vertices) and edges. Each node stores some data, and each edge connects two nodes. Here's a picture of a sample graph with 6 nodes and 6 edges:

<img src="/Users/alberto/Devel/graph-book-reviews/doc/Graph.png" alt="Graph" style="zoom:50%;" />

On top of this, a labelled property graph model adds a few extra features:

- Each node and each edge have a *label* that identifies their role in the data model.
- Each node and each edge store a set of key-value *properties*.
- Each edge has a direction (this is not necessarily the case with a general graph, which could be either directional or adirectional.)

This is essentially the model that production graph databases, such as [Neo4j](https://neo4j.com/developer/guide-data-modeling/) and [Titan](http://s3.thinkaurelius.com/docs/titan/1.0.0/schema.html), use.

So how can we model the book review domain as a labelled property graph? Here's my first take at it:

<img src="/Users/alberto/Devel/graph-book-reviews/doc/Graph1.png" alt="Graph1" style="zoom:50%;" />

Nodes have labels (e.g. "Book") and some properties -- for eample, the `Book` node has two properties, `isbn` and `title`. Node properties are just the same as the attributes we previously identified in our ER diagram.

Edges have labels too (e.g. "Reviewed"). Note how some edges also have properties -- for example, the `Reviewed` edge has the `rating` property, which stores the rating a user gave to a book, and the `Published by` edge has a `year` property, i.e. the year the book was published. Other edges don't have properties -- for example, the `In City` edge that connects a user to the city where they live: we don't need to store any extra data on that relationship.

The picture above represents the *schema* of our graph model. When we create a graph instance and store some data in it, here is how it could be pictured (note that this is just a subset of the entire graph):

<img src="/Users/alberto/Devel/graph-book-reviews/doc/Graph2.png" alt="Graph2" style="zoom:50%;" />

This could have easily been drawn on a whiteboard. In fact, when it comes to connected data, it's quite natural and intuitive to model it as a graph.

## Implementing a labelled property graph

Let's see how we can implement a labelled property graph in Java. We start by defining the `Node` and `Edge` classes as well as a common superclass, `GraphElement`, which stores each graph element's label and properties:

```
public class GraphElement {
    public final String label;
    public final Map<String, Object> properties = new HashMap<>();

    public GraphElement(final String label) {
        this.label = label;
    }
}

public class Node extends GraphElement {
    public final String id;
    public final List<Edge> outgoingEdges = new ArrayList<>();
    public final List<Edge> incomingEdges = new ArrayList<>();

    public Node(final String id, final String label) {
        super(label);
        this.id = id;
    }
}

public class Edge extends GraphElement {
    public final Node source;
    public final Node target;

    public Edge(final String label, final Node source, final Node target) {
        super(label);
        this.source = source;
        this.target = target;
    }
}
```

Nothing surprising here, except maybe those `outgoingEdges` and `incomingEdges` fields in the `Node` class. This is essentially how we connect nodes and edges together, and how we'll navigate the graph to extract meaningful data (we'll see that soon.)

I chose to represent `outgoingEdges` and `incomingEdges` as lists, but these might as well be sets (e.g. hash sets or tree sets) or other structures. This choice depends on a number of factors (e.g. do we need to guarantee uniqueness of each edge? Do we need to efficiently find edges based on some of their properties?) However, these consideration are beyond the scope of this article -- if you are looking for efficient in-memory graph databases, you might want to consider products such as [Memgraph](https://memgraph.com/) or [Neo4j embedded](https://neo4j.com/docs/java-reference/current/java-embedded/). For this example I decided to keep things simple and use plain array lists.

Also, note that each node has an `id` field. Unsurprisingly, the primary function of this field is to guarantee uniqueness of each node.

Next, we'll define the `Graph` class which exposes methods for creating nodes and edges:

```
public class Graph {
    private final Map<String, Node> nodeIdToNode = new HashMap<>();
    private final Map<String, Set<Node>> nodeLabelToNodes = new HashMap<>();

    public Node createNode(String id, String label) {
        if (nodeIdToNode.containsKey(id)) { throw new DuplicateNodeException(id); }

        final Node n = new Node(id, label);
        
        nodeIdToNode.put(id, n);
        nodeLabelToNodes.get(label).add(n);
        
        return n;
    }

    public Edge createEdge(String label, String fromNodeId, String toNodeId) {
        final Node fromNode = getNode(fromNodeId);
        final Node toNode = getNode(toNodeId);
        
        final Edge e = new Edge(label, fromNode, toNode);
        
        fromNode.outgoingEdges.add(e);
        toNode.incomingEdges.add(e);

        return e;
    }
}
```

The two maps, `nodeIdToNode` and `nodeLabelToNode`, allow us retrieve nodes by their id and label, respectively. This will become especially useful when we start writing queries.

We define a `BookReviewGraph` class as a subclass of `Graph`:

```
public class BookReviewsGraph extends Graph {

    // Node labels
    public static final String NODE_BOOK = "book";
    public static final String NODE_AUTHOR = "author";
    public static final String NODE_PUBLISHER = "publisher";
    //...
    
    // Edge labels
    public static final String EDGE_WRITTEN_BY = "writtenBy";
    public static final String EDGE_PUBLISHED_BY = "publishedBy";
    // ...

    private void addBook(String isbn, String title) {
        Node node = addNode(isbn, NODE_BOOK); // Use ISBNs as book node ids
        node.properties.put("isbn", isbn);
        node.properties.put("title", title);
    }

    private void addWrittenBy(String isbn, String authorName) {
        String id = "author-" + authorName;
        Node node = createNodeIfAbsent(id, NODE_AUTHOR);
        node.properties.put("name", authorName);
        addEdge(EDGE_WRITTEN_BY, isbn, id);
    }
    
    private void addPublishedBy(String isbn, String publisher, int yearOfPublication) {
        String id = "publisher-" + publisher;
        createNodeIfAbsent(id, NODE_PUBLISHER);
        Edge edge = addEdge(EDGE_PUBLISHED_BY, isbn, id);
        edge.properties.put("year", yearOfPublication);
    }
    
    // ...
}

```

Note that we don't use random-generated ids (e.g. UUIDs); instead, we repurpose entity data as ids. This has a couple of advantages:

- it's easier to debug, and
- it's easier to query (if we're looking for books by author, we can just get the node with id = `"author-" + authorName`)

Regarding the second point, we could have created specific HashMaps that associate a property value with a set of nodes. For example, we could have HashMaps such as:

```
Map<String, Set<Node>> authorsByName;
Map<String, Set<Node>> booksByTitle;
```

and we can keep these HashMaps updated when we create author or book nodes. This is a perfectly valid approach -- I use it to store 

## Sample queries



## Conclusion

