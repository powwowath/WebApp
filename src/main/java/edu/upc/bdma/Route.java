package edu.upc.bdma;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

import java.util.ArrayList;

/**
 * Created by Gerard on 25/05/2017.
 */
@NodeEntity
public class Route {

    @GraphId
    private Long id;
    private String name;
    private String departure;
    private String departureCountry;
    private boolean isRound;
    private int nStopOver;
    private double distance;
//    @Transient
    private String lon[];
//    @Transient
    private String lat[];

    private double indTourist;
    private double indNightlife;
    private double indCulture;
    private double indBeach;
    private double indMountain;

    private boolean isBigCities;
    private boolean isSmallCities;

    @JsonIgnore
    @Relationship(type="likes", direction = Relationship.INCOMING)
    private ArrayList<User> users;


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


    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public int getnStopOver() {
        return nStopOver;
    }

    public void setnStopOver(int nStopOver) {
        this.nStopOver = nStopOver;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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

    public double getIndTourist() {
        return indTourist;
    }

    public void setIndTourist(double indTourist) {
        this.indTourist = indTourist;
    }

    public double getIndNightlife() {
        return indNightlife;
    }

    public void setIndNightlife(double indNightlife) {
        this.indNightlife = indNightlife;
    }

    public double getIndCulture() {
        return indCulture;
    }

    public void setIndCulture(double indCulture) {
        this.indCulture = indCulture;
    }

    public double getIndBeach() {
        return indBeach;
    }

    public void setIndBeach(double indBeach) {
        this.indBeach = indBeach;
    }

    public double getIndMountain() {
        return indMountain;
    }

    public void setIndMountain(double indMountain) {
        this.indMountain = indMountain;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public boolean isRound() {
        return isRound;
    }

    public void setRound(boolean round) {
        isRound = round;
    }

    public String getDepartureCountry() {
        return departureCountry;
    }

    public void setDepartureCountry(String departureCountry) {
        this.departureCountry = departureCountry;
    }

    public boolean isBigCities() {
        return isBigCities;
    }

    public void setBigCities(boolean bigCities) {
        isBigCities = bigCities;
    }

    public boolean isSmallCities() {
        return isSmallCities;
    }

    public void setSmallCities(boolean smallCities) {
        isSmallCities = smallCities;
    }
}