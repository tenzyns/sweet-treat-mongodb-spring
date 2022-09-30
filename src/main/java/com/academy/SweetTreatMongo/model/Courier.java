package com.academy.SweetTreatMongo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.*;
import java.math.BigDecimal;

import static org.springframework.data.mongodb.core.mapping.FieldType.DECIMAL128;

@Getter
@Setter
@Document
public class Courier {

    @Id
    @JsonIgnore //Doesn't show this field when courier object is called for display
    private String id;
    @NotBlank(message = "Name must be provided.")
    @Size(min=3, message = "Name must be of 3 or more characters")
    private String name;
    @JsonIgnore
    @NotBlank(message = "A valid start-time must be provided.")
    private String startTime;
    @JsonIgnore
    @NotBlank(message = "A valid end-time must be provided.")
    private String endTime;
    @NotNull(message = "A true or false value must be provided.")
    private Boolean isBoxRefrigerated;
    @JsonIgnore
    @Min(value=1, message = "Max distance must be at least 1.")
    private double maxDistance;
    @NotNull(message = "Rate value must be provided.")
    @Positive(message = "Rate value must be a positive value.")
    @Field(targetType = DECIMAL128)
    private BigDecimal ratePerMile;

    public Courier(){};

    public Courier(String name, String startTime, String endTime, Boolean isBoxRefrigerated, double maxDistance, double ratePerMile) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBoxRefrigerated = isBoxRefrigerated;
        this.maxDistance = maxDistance;
        this.ratePerMile = BigDecimal.valueOf(ratePerMile);
    }

    @Override
    public String toString() {
        return "[Courier: " + name + ", cost per mile: £" + ratePerMile + " delivers up to " + maxDistance + " miles]";
    }

}
