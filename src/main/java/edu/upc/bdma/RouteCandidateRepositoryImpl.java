package edu.upc.bdma;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Gerard on 25/05/2017.
 */
class RouteCandidateRepositoryImpl implements RouteCandidateRepositoryCustom {
    public List<Route> doQuery(
            String dep,
            String depCountry,
            boolean isRound,
            int steps,
            boolean onlyBigCities,
            boolean onlySmallCities,
            boolean checkCulture,
            boolean checkNight,
            boolean checkBeach,
            boolean checkMountain,
            boolean checkTurist,
            String distance
            ) {
        Neo4JService service = new Neo4JService();

        return service.getRoutes(
                dep,
                depCountry,
                isRound,
                steps,
                onlyBigCities,
                onlySmallCities,
                checkCulture,
                checkNight,
                checkBeach,
                checkMountain,
                checkTurist,
                distance
        );
    }
}
