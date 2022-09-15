package com.academy.SweetTreatMongo.web;

import com.academy.SweetTreatMongo.exception.CourierNotFoundException;
import com.academy.SweetTreatMongo.model.Courier;
import com.academy.SweetTreatMongo.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/sweettreat")
public class CourierController {
    private CourierService courierService;
    @Autowired
    public void setCourierService(CourierService courierService) {
        this.courierService = courierService;
    }

//    Fetching the cheapest courier based on the given criteria
    @GetMapping("/cheapest/{time}/{distance}/{refrigeration}")
    public ResponseEntity<Courier> getCheapestCourier(@PathVariable("time") String time, @PathVariable("distance") double distance, @PathVariable("refrigeration") boolean refrigeration) {
        try {
            Courier cheapest = courierService.cheapestCourier(time, distance, refrigeration);
            return new ResponseEntity<>(cheapest, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier not found", e);
        }
    }

//    Registering or creating a new courier
    @PostMapping("/courier")
    public ResponseEntity<Courier> registerCourier(@Validated @RequestBody Courier courier) {
            return new ResponseEntity<>(courierService.addCourier(courier), HttpStatus.CREATED);

    }

//    Fetching the list of couriers who meet the criteria, sorted in order of price
    @GetMapping("/list/{time}/{distance}/{refrigeration}")
    public ResponseEntity<List<Courier>> listCouriers(@PathVariable("time") String time, @PathVariable("distance") double distance, @PathVariable("refrigeration") boolean refrigeration) {
        try {
            List<Courier> list = courierService.listCouriers(time, distance, refrigeration);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (CourierNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Courier not found!", exception);
        }
    }

//    Deleting a courier by their id
    @DeleteMapping("courier/{id}")
    public String deleteCourier(@PathVariable("id") String id) {
        try {
            courierService.deleteCourierById(id);
            return "Courier with id:" + id + " deleted successfully";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier id not found!");
        }
    }

//    Fetching a courier by their id
    @GetMapping("/courier/{id}")
    public ResponseEntity<Courier> getCourier(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(courierService.findCourier(id), HttpStatus.OK);
        } catch (CourierNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier Not found", exception);
        }
    }
//    Updating courier details by their id
    @PutMapping("/courier/{id}")
    public ResponseEntity<Courier> updateCourier(@RequestBody Courier newDetail, @PathVariable String id) {
        try{
            return new ResponseEntity<>(courierService.updateCourierById(newDetail, id), HttpStatus.OK);
        } catch (CourierNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier id not found");
        }
    }

}
