package com.academy.SweetTreatMongo.repository;

import com.academy.SweetTreatMongo.model.Courier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface CourierRepository extends MongoRepository<Courier, String> {

    //the query to return only the couriers with refrigerated box and who meet the order requirement
    @Query(value = "{startTime: {$lte: ?0}, endTime: {$gt: ?0}, maxDistance: {$gte: ?1}, isBoxRefrigerated: true}}", fields = "{'name': 1, 'ratePerMile': 1, 'isBoxRefrigerated': 1, '_id': 0}", sort = "{ratePerMile:1}")
    List<Courier> refrigeratedCourierList(String time, double distance);

    //the query to return all couriers that meet the order requirement
    @Query(value = "{startTime: {$lte: ?0}, endTime: {$gt: ?0}, maxDistance: {$gte: ?1}}", sort = "{ratePerMile:1}")
    List<Courier> availableCourierList(String time, double distance);

//    query to return the cheapest courier with refrigerated box
    @Aggregation(pipeline = {
            "{ '$match': {startTime: {$lte: ?0}, endTime: {$gt: ?0}, maxDistance: {$gte: ?1}, isBoxRefrigerated: true}}",
            "{ '$project': {'name'=:1, 'ratePerMile':1, 'isBoxRefrigerated':1, '_id':0}}",
            "{ '$sort': {ratePerMile:1}}",
            "{ '$limit': ?2}" })
        List<Courier> cheapestCourierRefrigerated(String time, double distance, int limit);

}