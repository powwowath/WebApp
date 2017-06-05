package edu.upc.bdma;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<User>  readUsers() {
        return IteratorUtils.toList(userRepository.findAll().iterator());
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus( HttpStatus.CREATED )
    @ResponseBody
    public User insertUser(@RequestBody User user) {
        User temp = userRepository.findByName(user.getName());
        if(temp == null){
            userRepository.save(user);
        }
        return user;
    }

    @RequestMapping("/count")
    public long countRoutes() {
        return userRepository.count();
    }

}
