package edu.upc.bdma;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/routecandidates")
public class RouteCandidateController {
    @Autowired
    private RouteCandidateRepository routeCandidateRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<Route> rc (@RequestParam(value="departureCity") String dep,
                           @RequestParam(value="departureCountry") String depCountry,
                           @RequestParam(value="isRound") boolean isRound,
                           @RequestParam(value="steps", defaultValue="3") int steps,
                           @RequestParam(value="bigCities", defaultValue = "false") boolean onlyBigCities,
                           @RequestParam(value="smallCities", defaultValue = "false") boolean onlySmallCities,
                           @RequestParam(value="checkCulture", defaultValue = "false") boolean checkCulture,
                           @RequestParam(value="checkNight", defaultValue = "false") boolean checkNight,
                           @RequestParam(value="checkBeach", defaultValue = "false") boolean checkBeach,
                           @RequestParam(value="checkMountain", defaultValue = "false") boolean checkMountain,
                           @RequestParam(value="checkTurist", defaultValue = "false") boolean checkTurist,
                           @RequestParam(value="distance") String distance
                           ) {


        return IteratorUtils.toList(routeCandidateRepository.doQuery(
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
        ).iterator());

        // MATCH (departure:City)<-[]-(dep:Airport)-[r1:flights]->(s1:Airport)-[]->(dest:City) RETURN departure.name, dest.name, r1.distance LIMIT 10;

//        String query = "MATCH (departure:City)<-[]-(dep:Airport)-[r1:flights]->(s1:Airport)-[]->(dest:City) RETURN departure.name, dest.name, r1.distance LIMIT 10";
//        List<RouteCandidateOLD> rc = routeCandidateRepository.querySearch(query);
/*
        EmbeddedDriver embeddedDriver = (EmbeddedDriver) Components.driver();
        GraphDatabaseService databaseService = embeddedDriver.getGraphDatabaseService();
        databaseService.
  */
        //routeCandidateRepository.customSearch("MATCH (departure:City)<-[]-(dep:Airport)-[r1:flights]->(s1:Airport)-[]->(dest:City) RETURN departure.name AS departureCity, dest.name AS arrivalCity, r1.distance LIMIT 10");
//        System.out.println("Results: " + IteratorUtils.toList(routeCandidateRepository.cypherQuery(dep, dest, steps).iterator()).size());
//        return IteratorUtils.toList(routeCandidateRepository.doQuery(dep).iterator());

//        System.out.println("Results: " + IteratorUtils.toList(routeCandidateRepository.doQuery(dep, dest, steps).iterator()).size());
   }
}
