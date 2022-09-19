package com.academy.SweetTreatMongo.repository;

import com.academy.SweetTreatMongo.model.Courier;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CourierRepository extends MongoRepository<Courier, String> {
}
