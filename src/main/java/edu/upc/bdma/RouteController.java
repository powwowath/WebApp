package edu.upc.bdma;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {
    @Autowired
    private RouteRepository routeRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<Route>  readRoutes() {
        return IteratorUtils.toList(routeRepository.findAll().iterator());
    }

    @RequestMapping("/count")
    public long countRoutes() {
        return routeRepository.countFlights();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus( HttpStatus.CREATED )
    @ResponseBody
    public Route insertRoute(@RequestBody Route route) {
        routeRepository.save(route);
        return route;
    }
}
