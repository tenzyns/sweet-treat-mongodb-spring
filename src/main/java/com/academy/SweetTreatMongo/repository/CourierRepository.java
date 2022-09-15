package com.academy.SweetTreatMongo.repository;

import com.academy.SweetTreatMongo.model.Courier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

public interface CourierRepository extends MongoRepository<Courier, String> {
}
