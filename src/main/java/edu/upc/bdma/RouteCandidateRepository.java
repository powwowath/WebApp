package edu.upc.bdma;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RouteCandidateRepository extends GraphRepository<RouteCandidate> {

//    @Query("%:query%")
//    List<RouteCandidate> querySearch(@Param("query") String query);


    @Query("MATCH (dep:City)<-[]-(:Airport)-[r1:flights]->(:Airport)-[]->(s1:City)<-[]-(:Airport)-[r2:flights]->(:Airport)-[]->(arr:City) WHERE dep.name = {dep} RETURN dep.name AS departureCity, s1.name AS s1City, arr.name AS arrivalCity LIMIT 12")
    List<RouteCandidate> cypherQuery(@Param("dep") String dep, @Param("arr") String arr, @Param("steps") int nSteps);






    //@Query("MATCH (dep:City)<-[]-(a1:Airport)-[r1:flights]->(a2:Airport)-[]->(s1:City)<-[]-(a3:Airport)-[r2:flights]->(a4:Airport)-[]->(arr:City) WHERE dep.name = \"Gerona\" RETURN dep.name, s1.name, arr.name, r1.distance LIMIT 10;")
    //Stream<RouteCandidate> findByUserParams(String dep, String arr, int nSteps);


/*
    EmbeddedDriver embeddedDriver = (EmbeddedDriver) Components.driver();
    GraphDatabaseService databaseService = embeddedDriver.getGraphDatabaseService();
*/
/*
    @Autowired
    Neo4jOperations template;

    public User findBySocialUser(String providerId, String providerUserId) {
        String query = "MATCH (n:SocialUser{providerId:{providerId}, providerUserId:{providerUserId}})<-[:HAS]-(user) RETURN user";

        final Map<String, Object> paramsMap = ImmutableMap.<String, Object>builder().
                put("providerId", providerId).
                put("providerUserId", providerUserId).
                build();

        Map<String, Object> result = template.query(query, paramsMap).singleOrNull();
        return (result == null) ? null : (User) template.getDefaultConverter().convert(result.get("user"), User.class);
    }


    List<RouteCandidate> findByParams(@Param("dep") String dep, @Param("arr") String arr, @Param("steps") int nSteps);

        String query = "MATCH (dep:City)<-[]-(:Airport)-[r1:flights]->(:Airport)-[]->(s1:City)<-[]-(:Airport)-[r2:flights]->(:Airport)-[]->(arr:City) WHERE dep.name = {dep} RETURN dep.name, s1.name, arr.name, r1.distance LIMIT 12";
        Result<Map<String, Object>> result = neo4jTemplate.query(query, Collections.emptyMap());
        for(
            Map<String, Object> r :result.slice(1,3))

            {
                Product product = (Product) neo4jTemplate.getDefaultConverter().convert(r.get("n"), Product.class);
                System.out.println(product.getUuid());
            }
        }
*/
}