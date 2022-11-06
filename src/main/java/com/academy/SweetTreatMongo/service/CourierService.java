package com.academy.SweetTreatMongo.service;



import com.academy.SweetTreatMongo.model.Courier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CourierService {
//    fetching list of all couriers
    List<Courier> allCouriers();

// fetching all available couriers in a list in order of increasing price
    List<Courier> listCouriers(String time, double distance, boolean refrigeration);
    List<Courier> cheapestCourier(String time, double distance, boolean refrigeration);

    Courier findCourier(String id);
//    adding a new courier
    Courier addCourier(Courier courier);

    void deleteCourierById(String id);

    //    Updating operation
    Courier updateCourierById(Courier newDetail, String id);

}
