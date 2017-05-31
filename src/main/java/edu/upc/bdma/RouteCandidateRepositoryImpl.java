package edu.upc.bdma;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Gerard on 25/05/2017.
 */
class RouteCandidateRepositoryImpl implements RouteCandidateRepositoryCustom {
    @PersistenceContext(unitName="defaultPersistenceUnit")
//    @PersistenceContext(unitName="defaultPersistenceUnit")
    private EntityManager em;

    public List<Route> doQuery(String dep, String dest, int steps) {
//        Query query = em.createQuery("MATCH (c:City) RETURN c LIMIT 2");
//        List results = query.getResultList();
        Neo4JService service = new Neo4JService();
        return service.getRoutes(dep, dest, steps);
    }
}
