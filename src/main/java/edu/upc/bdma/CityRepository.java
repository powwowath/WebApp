package edu.upc.bdma;

import org.springframework.data.neo4j.repository.GraphRepository;

public interface CityRepository extends GraphRepository<City> {

    City findByName(String name);
}