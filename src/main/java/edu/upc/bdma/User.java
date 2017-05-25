package edu.upc.bdma;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class User {

    @GraphId private Long id;
    private String name;

    private User() {
        // Empty constructor required as of Neo4j API 2.0.5
    };

    public User(String name) {
        this.name = name;
    }

    /**
     * Neo4j doesn't REALLY have bi-directional relationships. It just means when querying
     * to ignore the direction of the relationship.
     * https://dzone.com/articles/modelling-data-neo4j
     */
    @Relationship(type = "likes", direction = Relationship.UNDIRECTED)
    public Set<Route> routes;

    public void likes(Route route) {
        if (routes == null) {
            routes = new HashSet<>();
        }
        routes.add(route);
    }

    public String toString() {
        return this.name + " likes => "
                + Optional.ofNullable(this.routes).orElse(
                Collections.emptySet()).stream().map(
                route -> route.getName()).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}