package edu.upc.bdma;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Airport {

    @GraphId private Long id;
    private String name;
    private String country;

    public Airport() {
        // Empty constructor required as of Neo4j API 2.0.5
    };

    public Airport(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}