package edu.upc.bdma;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {
    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<Route>  readRoutes() {
        return IteratorUtils.toList(routeRepository.findAll().iterator());
    }

    @RequestMapping("/count")
    public long countRoutes() {
        return routeRepository.countFlights();
    }

    @RequestMapping("/countLikes")
    public long countRoutesLikes() {
        return routeRepository.countRoutesLikes();
    }

    @RequestMapping("/countStored")
    public long countStored() {
        return routeRepository.countStored();
    }

    @RequestMapping("/routeUserLikes")
    public long routeUserLikes(@RequestParam(value="userName") String userName) {
        return routeRepository.countUserLikes(userName);
    }



    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus( HttpStatus.CREATED )
    @ResponseBody
    public Route insertRoute(@RequestBody UserRoute userRoute) {
        Route neoRoute = routeRepository.findByName(userRoute.getRoute().getName());
        User currentUser = userRepository.findByName(userRoute.getUserName());
        ArrayList<User> userList = new ArrayList<>();
        if (neoRoute == null){
            neoRoute = userRoute.getRoute();
        } else if (neoRoute.getUsers() != null){
            userList = neoRoute.getUsers();
        }
        userList.add(currentUser);
        neoRoute.setUsers(userList);

        routeRepository.save(neoRoute);
        return neoRoute;
    }
}
