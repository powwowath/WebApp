package edu.upc.bdma;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface RouteRepository extends GraphRepository<Route> {

    Route findByName(String name);

    @Query ("MATCH ()-[n:flights]->() RETURN count(n)")
    Long countFlights ();
}