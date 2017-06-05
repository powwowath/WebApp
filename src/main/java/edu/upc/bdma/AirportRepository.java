package edu.upc.bdma;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface AirportRepository extends GraphRepository<Airport> {

}