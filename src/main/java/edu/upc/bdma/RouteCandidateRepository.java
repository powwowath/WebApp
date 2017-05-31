package edu.upc.bdma;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Gerard on 25/05/2017.
 */
interface RouteCandidateRepository extends GraphRepository<Route>, RouteCandidateRepositoryCustom {

    @Query("MATCH (dep:City) WHERE dep.name = {dep} RETURN dep.name AS name LIMIT 12")
//    @Query("MATCH (dep:City)<-[]-(:Airport)-[r1:flights]->(:Airport)-[]->(s1:City)<-[]-(:Airport)-[r2:flights]->(:Airport)-[]->(arr:City) WHERE dep.name = {dep} RETURN dep.name AS name LIMIT 12")
    List<Route> cypherQuery(@Param("dep") String dep, @Param("arr") String arr, @Param("steps") int nSteps);

    public List<Route> doQuery(String dep, String dest, int steps);

}