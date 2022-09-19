package com.academy.SweetTreatMongo.service;

import com.academy.SweetTreatMongo.exception.CourierNotFoundException;
import com.academy.SweetTreatMongo.model.Courier;
import com.academy.SweetTreatMongo.repository.CourierRepository;
import com.academy.SweetTreatMongo.web.CourierController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;

@Service
public class CourierServiceImpl implements CourierService {
    @Autowired
    private final CourierRepository courierRepository;
    private BigDecimal courierCost;
    private static final Logger LOGGER = Logger.getLogger(CourierController.class.getName());

    static {
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler(CourierController.class.getSimpleName() + ".log");
            fileHandler.setFormatter(new SimpleFormatter());
            Filter filterAll = s -> true;
            fileHandler.setFilter(filterAll);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.addHandler(fileHandler);
    }

    public CourierServiceImpl(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }
//list all couriers
    @Override
    public List<Courier> allCouriers() {
        return courierRepository.findAll();
    }
//    Method for selecting couriers that satisfy customer requirement
    public List<Courier> availableCouriers(String time, double distance, boolean refrigeration) {
        //if available return screenedCouriers else return failMsg;
        List<Courier> list = (List<Courier>) courierRepository.findAll();
        LocalTime orderTime = LocalTime.parse(time);
        ArrayList<Courier> screenedCouriers = new ArrayList<>();

        for (Courier i : list) {
            if(refrigeration && orderTime.isAfter(LocalTime.parse(i.getStartTime())) && orderTime.isBefore(LocalTime.parse(i.getEndTime())) &&
                    i.getIsBoxRefrigerated() && i.getMaxDistance() >= distance) {
                screenedCouriers.add(i);
            } else if(!refrigeration && orderTime.isAfter(LocalTime.parse(i.getStartTime())) && orderTime.isBefore(LocalTime.parse(i.getEndTime()))
                    && i.getMaxDistance() >= distance) {
                screenedCouriers.add(i);
            }
        }
        return screenedCouriers;
    }

    //------------ Get operation for the list of couriers in order of price  -------
    @Override
    public List<Courier> listCouriers(String time, double distance, boolean refrigeration) {
        List<Courier> screenedList = availableCouriers(time, distance, refrigeration);
        List<Courier> sortedList;

        if (screenedList.size() == 0) {//if no courier satisfies the requirement

            LOGGER.log(Level.WARNING, "Unable to select a suitable courier for this order, made at " + time + ", for a distance of " + distance + " miles, refrigeration requirement: " + refrigeration);

            throw new CourierNotFoundException("Courier not available for your requirement due to time or distance constraint");

        } else {
            System.out.println("<---Courier list in order of price ---> ");
            sortedList = screenedList.stream().sorted(Comparator.comparing(Courier::getRatePerMile)).collect(Collectors.toList());
            sortedList.forEach(System.out::println);

            LOGGER.log(Level.INFO, "The list of couriers in order of their price with cheapest 1st: \n" + sortedList);
            return sortedList;
        }

    }

//------------------- Get operation for the cheapest courier -------------
    @Override
    public Courier cheapestCourier(String time, double distance, boolean refrigeration) {
        List<Courier> screenedList = availableCouriers(time, distance, refrigeration);
        int size = screenedList.size();
        if (size == 0) {
            LOGGER.log(Level.WARNING, "No courier found for this order with time: " +time + ", delivery distance: " +distance + " miles, refrigeration requirement: " + refrigeration);
            throw new CourierNotFoundException("Courier not available due to time and/or distance constraint");
        } else {
            Courier cheapest = screenedList.get(0);
            for (int j = 1; j < size; j++) {
                if (screenedList.get(j).getRatePerMile().compareTo(cheapest.getRatePerMile()) < 0) {
                    cheapest = screenedList.get(j);
                }
            }
            setCourierCost(BigDecimal.valueOf(distance).multiply(cheapest.getRatePerMile()).setScale(2, RoundingMode.HALF_EVEN));
            LOGGER.log(Level.INFO, "Most suitable courier for this order is " + cheapest.getName() + " for the request time " + time + " and will cost £" + courierCost);
            return cheapest;
        }
    }

    private void setCourierCost(BigDecimal cost){
        this.courierCost = cost;
    }

//--------------------- Get operation by id  ----------------------
    @Override
    public Courier findCourier(String id) {
        Optional<Courier> optionalCourier = courierRepository.findById(id);
        if(optionalCourier.isPresent())
            return optionalCourier.get();
        else
            throw new CourierNotFoundException("No courier found!");
    }

//-----------------    Create operation ----------------
    @Override
    public Courier addCourier(Courier courier) {
        LOGGER.log(Level.INFO, "New courier " + courier.getName() + " has been added successfully to the system.");
        return courierRepository.save(courier);
    }

//--------------- Delete operation-----------------
    @Override
    public void deleteCourierById(String id) {
        courierRepository.deleteById(id);
        LOGGER.log(Level.INFO, "Courier with id:" + id + " has been deleted successfully.");
    }

//----------------- Update operation  --------------------------
    @Override
    public Courier updateCourierById(Courier newDetail, String id) {
//        fetching old courier by the given id
        Optional<Courier> optionalCourier = courierRepository.findById(id);
        if (optionalCourier.isPresent()) {
           Courier oldCourier = optionalCourier.get();
//        Checking new courier's name is not null and not an empty string
            if (Objects.nonNull(newDetail.getName()) && !"".equalsIgnoreCase(newDetail.getName())) {
                oldCourier.setName(newDetail.getName()); //updating the name with new name
            }
            if (Objects.nonNull(newDetail.getStartTime()) && !"".equals(newDetail.getStartTime())) {
                oldCourier.setStartTime(newDetail.getStartTime());
            }
            if (Objects.nonNull(newDetail.getEndTime()) && !"".equals(newDetail.getEndTime())) {
                oldCourier.setEndTime(newDetail.getEndTime());
            }
            if (Objects.nonNull(newDetail.getIsBoxRefrigerated())) {
                oldCourier.setIsBoxRefrigerated(newDetail.getIsBoxRefrigerated()); //updating the name with new name
            }
            if (newDetail.getMaxDistance() >= 1) {
                oldCourier.setMaxDistance(newDetail.getMaxDistance());
            }
            if (Objects.nonNull(newDetail.getRatePerMile()) && newDetail.getRatePerMile().doubleValue() > 0) {
                oldCourier.setRatePerMile(newDetail.getRatePerMile());
            }
            LOGGER.log(Level.INFO, "Courier with id:" +id + " has their details updated successfully.");
            return courierRepository.save(oldCourier);
        } else {
            LOGGER.log(Level.WARNING, "Courier with this id:" +id +" doesn't exist!");
            throw new CourierNotFoundException("Courier with the id" + id + " not found");
        }
    }

}
