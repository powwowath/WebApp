package edu.upc.bdma;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cities")
public class CityController {
    @Autowired
    private CityRepository cityRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<City> readCity() {
        return IteratorUtils.toList(cityRepository.findAll(new Sort(Sort.Direction.ASC, "name")).iterator());
    }

    @RequestMapping("/count")
    public long countCities() {
        return cityRepository.count();
    }

}
