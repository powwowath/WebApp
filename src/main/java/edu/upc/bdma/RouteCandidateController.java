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
    public List<Route> rc (@RequestParam(value="departureCity") String dep, @RequestParam(value="destinationCity") String dest, @RequestParam(value="steps", defaultValue="3") int steps) {

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

        System.out.println("Results: " + IteratorUtils.toList(routeCandidateRepository.doQuery(dep, dest, steps).iterator()).size());
        return IteratorUtils.toList(routeCandidateRepository.doQuery(dep,dest,steps).iterator());

        //return new RouteCandidateOLD();
    }
}
