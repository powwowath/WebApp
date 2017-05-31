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
public class RouteOLD {

    @GraphId private Long id;
    private String name;

    private RouteOLD() {
        // Empty constructor required as of Neo4j API 2.0.5
    };

    public RouteOLD(String name) {
        this.name = name;
    }

    /**
     * Neo4j doesn't REALLY have bi-directional relationships. It just means when querying
     * to ignore the direction of the relationship.
     * https://dzone.com/articles/modelling-data-neo4j
     */
    @Relationship(type = "is", direction = Relationship.UNDIRECTED)
    public Set<RouteType> routeTypes;

    public void is(RouteType routeType) {
        if (routeTypes == null) {
            routeTypes = new HashSet<>();
        }
        routeTypes.add(routeType);
    }

    public String toString() {

        return this.name + " is => "
                + Optional.ofNullable(this.routeTypes).orElse(
                Collections.emptySet()).stream().map(
                routeType -> routeType.getName()).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}