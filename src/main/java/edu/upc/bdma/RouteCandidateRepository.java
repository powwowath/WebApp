package edu.upc.bdma;

import org.springframework.data.neo4j.repository.GraphRepository;
import java.util.List;

/**
 * Created by Gerard on 25/05/2017.
 */
interface RouteCandidateRepository extends GraphRepository<Route>, RouteCandidateRepositoryCustom {

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
    );

}