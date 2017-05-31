package edu.upc.bdma;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * Created by Gerard on 25/05/2017.
 */
@NodeEntity
public class Route {

    @GraphId
    Long id;
    String name;
    String lon[];
    String lat[];

//    @Relationship(type="ACTS_IN", direction = Relationship.INCOMING)
//    City[] cities;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getLon() {
        return lon;
    }

    public void setLon(String[] lon) {
        this.lon = lon;
    }

    public String[] getLat() {
        return lat;
    }

    public void setLat(String[] lat) {
        this.lat = lat;
    }
}