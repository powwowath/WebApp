package edu.upc.bdma;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface RouteRepository extends GraphRepository<Route> {

    Route findByName(String name);

    @Query ("MATCH ()-[n:flights]->() RETURN count(n)")
    Long countFlights ();

    @Query ("MATCH ()-[n:likes]->() RETURN count(n)")
    Long countRoutesLikes ();

    @Query ("MATCH (u)-[n:likes]->() WHERE u.name = {0} RETURN count(n)")
    Long countUserLikes (String userName);

    @Query ("MATCH (n:Route) RETURN count(n)")
    Long countStored ();

}